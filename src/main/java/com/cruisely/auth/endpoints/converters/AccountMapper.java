package com.cruisely.auth.endpoints.converters;

import org.apache.commons.codec.digest.DigestUtils;
import com.cruisely.entities.auth.AccessLevel;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.auth.Account;
import com.cruisely.entities.auth.Address;
import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.auth.accesslevels.Client;
import com.cruisely.entities.auth.wrappers.LanguageTypeWrapper;
import com.cruisely.exceptions.AccountManagerException;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.ETagException;
import com.cruisely.auth.dto.*;
import com.cruisely.auth.dto.changedata.*;
import com.cruisely.auth.dto.detailsview.AccessLevelDetailsViewDto;
import com.cruisely.auth.dto.detailsview.AccountDetailsViewDto;
import com.cruisely.auth.dto.detailsview.accesslevels.AdministratorDetailsViewDto;
import com.cruisely.auth.dto.detailsview.accesslevels.BusinessWorkerDetailsViewDto;
import com.cruisely.auth.dto.detailsview.accesslevels.ClientDetailsViewDto;
import com.cruisely.auth.dto.detailsview.accesslevels.ModeratorDetailsViewDto;
import com.cruisely.auth.dto.registration.BusinessWorkerForRegistrationDto;
import com.cruisely.auth.dto.registration.ClientForRegistrationDto;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cruisely.common.I18n.ACCESS_LEVEL_DOES_NOT_EXIST_ERROR;

/**
 * Class responsible for mapping DTO objects to model/entity objects.
 */
public class AccountMapper {
    private AccountMapper() {
    }

    /**
     * Maps a ClientForRegistrationDto to a Client model object used for persistence.
     *
     * @param client DTO containing client registration data
     * @return Client entity populated from the DTO
     */
    public static Client extractClientFromClientForRegistrationDto(ClientForRegistrationDto client) {
        return new Client(extractAddressFromClientForRegistrationDto(client), client.getPhoneNumber());
    }

    /**
     * Maps a ClientForRegistrationDto to an Account model object used for persistence.
     *
     * @param client DTO containing client registration data
     * @return Account entity populated from the DTO
     */
    public static Account extractAccountFromClientForRegistrationDto(ClientForRegistrationDto client) {
        return new Account(client.getFirstName(), client.getSecondName(),
                client.getLogin(), client.getEmail().toLowerCase(), DigestUtils.sha256Hex(client.getPassword()),
                new LanguageTypeWrapper(client.getLanguageType()));
    }

    /**
     * Maps a ClientForRegistrationDto to an Address model object used for persistence.
     *
     * @param client DTO containing client registration data
     * @return Address entity populated from the DTO
     */
    public static Address extractAddressFromClientForRegistrationDto(ClientForRegistrationDto client) {
        return new Address(client.getAddressDto().getHouseNumber(),
                client.getAddressDto().getStreet(),
                client.getAddressDto().getPostalCode(),
                client.getAddressDto().getCity(),
                client.getAddressDto().getCountry());
    }

    /**
     * Maps a BusinessWorkerForRegistrationDto to a BusinessWorker model object used for persistence.
     *
     * @param bw DTO containing business worker registration data
     * @return BusinessWorker entity populated from the DTO
     */
    public static BusinessWorker extractBusinessWorkerFromBusinessWorkerForRegistrationDto(BusinessWorkerForRegistrationDto bw) {
        return new BusinessWorker(bw.getPhoneNumber(), true);
    }

    /**
     * Maps a BusinessWorkerForRegistrationDto to an Account model object used for persistence.
     *
     * @param bw DTO containing business worker registration data
     * @return Account entity populated from the DTO
     */
    public static Account extractAccountFromBusinessWorkerForRegistrationDto(BusinessWorkerForRegistrationDto bw) {
        return new Account(bw.getFirstName(), bw.getSecondName(),
                bw.getLogin(), bw.getEmail().toLowerCase(), DigestUtils.sha256Hex(bw.getPassword()),
                new LanguageTypeWrapper(bw.getLanguageType()));
    }

    /**
     * Maps an Account entity to an AccountDto transfer object.
     *
     * @param account Account entity to convert
     * @return AccountDto transfer representation of the account
     */
    public static AccountDto toAccountDto(Account account) {
        return new AccountDto(account.getLogin(), account.getFirstName(), account.getSecondName(),
                account.isDarkMode(), account.getEmail(), account.getLanguageType().getName(),
                account.getAccessLevels().stream()
                        .map(AccessLevel::getAccessLevelType)
                        .collect(Collectors.toSet()), account.getVersion());
    }

    /**
     * Builds an AccountDtoForList from an Account entity containing basic account information.
     *
     * @param account Account entity to convert
     * @return AccountDtoForList containing login, email, access levels and active status
     */
    public static AccountDtoForList toAccountListDto(Account account) throws BaseAppException {
        return new AccountDtoForList(account.getLogin(),
                account.getFirstName(), account.getSecondName(),
                account.getEmail(), account.isActive(),
                account.getVersion(),
                account.getAccessLevels().stream()
                        .map(AccessLevel::getAccessLevelType)
                        .collect(Collectors.toSet()));
    }

    /**
     * Maps an Account entity to an AccountDetailsViewDto transfer object.
     *
     * @param account Account entity to convert
     * @return AccountDetailsViewDto with full account details
     */
    public static AccountDetailsViewDto toAccountDetailsViewDto(Account account) throws BaseAppException {
        return new AccountDetailsViewDto(account.getFirstName(), account.getSecondName(), account.isDarkMode(), account.getLogin(),
                account.getEmail(), account.isConfirmed(), account.isActive(), account.getLanguageType().getName(),
                account.getAccessLevels().stream()
                        .map(AccountMapper::toAccessLevelDetailsViewDto)
                        .collect(Collectors.toSet()),
                account.getVersion());
    }

    /**
     * Maps an Account entity to an AccountDetailsViewDto transfer object, filtering only active access levels.
     *
     * @param account Account entity to convert
     * @return AccountDetailsViewDto with filtered access level details
     */
    public static AccountDetailsViewDto toAccountDetailsViewFilteringAccessLevels(Account account) throws BaseAppException {
        return new AccountDetailsViewDto(account.getFirstName(), account.getSecondName(), account.isDarkMode(), account.getLogin(),
                account.getEmail(), account.isConfirmed(), account.isActive(), account.getLanguageType().getName(),
                getAuthorizedAccessLevels(account)
                        .map(AccountMapper::toAccessLevelDetailsViewDto)
                        .collect(Collectors.toSet()),
                account.getVersion());
    }

    private static AccessLevelDetailsViewDto toAccessLevelDetailsViewDto(AccessLevel accessLevel) {
        switch (accessLevel.getAccessLevelType()) {
            case CLIENT:
                Client client = (Client) accessLevel;
                return new ClientDetailsViewDto(accessLevel.isEnabled(), toAddressDto(client.getHomeAddress()), client.getPhoneNumber(), client.getVersion());
            case BUSINESS_WORKER:
                BusinessWorker businessWorker = (BusinessWorker) accessLevel;
                return new BusinessWorkerDetailsViewDto(businessWorker.isEnabled(), businessWorker.getPhoneNumber(),
                        businessWorker.isConfirmed(), businessWorker.getCompany().getName(), businessWorker.getVersion());
            case MODERATOR:
                return new ModeratorDetailsViewDto(accessLevel.isEnabled(), accessLevel.getVersion());
            case ADMINISTRATOR:
                return new AdministratorDetailsViewDto(accessLevel.isEnabled(), accessLevel.getVersion());
            default:
                return null; // Statement will never execute unless new AccessLevel ENUM is added to the model
        }
    }

    private static Stream<AccessLevel> getAuthorizedAccessLevels(Account account) {
        return account.getAccessLevels().stream()
                .filter(accessLevel -> {
                    if (accessLevel instanceof BusinessWorker) {
                        BusinessWorker bw = (BusinessWorker) accessLevel;
                        return bw.isEnabled() && bw.isConfirmed();
                    }
                    return accessLevel.isEnabled();
                });
    }

    private static AddressDto toAddressDto(Address address) {
        return new AddressDto(address.getHouseNumber(), address.getStreet(),
                address.getPostalCode(), address.getCity(), address.getCountry());
    }

    /**
     * Converts an Account entity into OtherClientChangeDataDto used for client data change operations.
     *
     * @param account Account entity to convert
     * @return OtherClientChangeDataDto with client's current personal data
     */
    public static OtherClientChangeDataDto accountDtoForClientDataChange(Account account) {

        Client fromClient = (Client) account.getAccessLevels().stream().filter(accessLevel -> accessLevel.getAccessLevelType().equals(AccessLevelType.CLIENT)).collect(Collectors.toList()).get(0);
        OtherAddressChangeDto addressChangeDto = new OtherAddressChangeDto(fromClient.getHomeAddress().getHouseNumber(), fromClient.getHomeAddress().getStreet(),
                fromClient.getHomeAddress().getPostalCode(), fromClient.getHomeAddress().getCity(), fromClient.getHomeAddress().getCountry());
        return new OtherClientChangeDataDto(account.getLogin(), account.getVersion(), fromClient.getPhoneNumber(), addressChangeDto, fromClient.getVersion());

    }

    /**
     * Converts an Account entity into OtherBusinessWorkerChangeDataDto used for business worker data change operations.
     *
     * @param account Account entity to convert
     * @return OtherBusinessWorkerChangeDataDto with business worker's current data
     */
    public static OtherBusinessWorkerChangeDataDto accountDtoForBusinnesWorkerDataChange(Account account) {

        BusinessWorker fromBusinessWorker = (BusinessWorker) account.getAccessLevels().stream().filter(accessLevel -> accessLevel.getAccessLevelType().equals(AccessLevelType.BUSINESS_WORKER)).collect(Collectors.toList()).get(0);
        return new OtherBusinessWorkerChangeDataDto(account.getLogin(), account.getVersion(),
                fromBusinessWorker.getPhoneNumber(), fromBusinessWorker.getVersion()
        );

    }

    /**
     * Maps an OtherAccountChangeDataDto to an Account model object used for persistence (for moderator/administrator accounts).
     *
     * @param otherAccountChangeDataDto DTO with account change data
     * @return Account entity populated from the DTO
     */
    public static Account extractAccountFromOtherAccountChangeDataDto(OtherAccountChangeDataDto otherAccountChangeDataDto) {
        Account account = new Account();
        account.setLogin(otherAccountChangeDataDto.getLogin());
        account.setVersion(otherAccountChangeDataDto.getVersion());
        account.setFirstName(otherAccountChangeDataDto.getNewFirstName());
        account.setSecondName(otherAccountChangeDataDto.getNewSecondName());
        return account;
    }

    private static void setAccountChangeDataDtoFields(Account account, AccountChangeDataDto accountChangeDataDto, LocalDateTime time) {
        account.setLogin(accountChangeDataDto.getLogin());
        account.setVersion(accountChangeDataDto.getVersion());
        account.setFirstName(accountChangeDataDto.getNewFirstName());
        account.setSecondName(accountChangeDataDto.getNewSecondName());
        account.setAlteredBy(account);
        account.setAlterType(account.getAlterType());
        account.setLastAlterDateTime(time);
    }

    /**
     * Maps a ClientChangeDataDto to an Account model object used for persistence (client-specific data updates).
     *
     * @param clientChangeDataDto DTO containing client change data
     * @return Account entity with client access level updated
     */
    public static Account extractAccountFromClientChangeDataDto(ClientChangeDataDto clientChangeDataDto) {
        LocalDateTime now = LocalDateTime.now();

        Account account = new Account();
        setAccountChangeDataDtoFields(account, clientChangeDataDto, now);

        Address address = new Address(
                clientChangeDataDto.getNewAddress().getHouseNumber(),
                clientChangeDataDto.getNewAddress().getStreet(),
                clientChangeDataDto.getNewAddress().getPostalCode(),
                clientChangeDataDto.getNewAddress().getCity(),
                clientChangeDataDto.getNewAddress().getCountry()
        );

        Client client = new Client(address, clientChangeDataDto.getNewPhoneNumber());

        account.setAccessLevel(client);

        return account;
    }

    public static Account extractAccountFromBusinessWorkerChangeDataDto(BusinessWorkerChangeDataDto businessWorkerChangeDataDto) {
        LocalDateTime now = LocalDateTime.now();

        Account account = new Account();

        setAccountChangeDataDtoFields(account, businessWorkerChangeDataDto, now);

        BusinessWorker businessWorker = new BusinessWorker(businessWorkerChangeDataDto.getNewPhoneNumber(), true);

        account.setAccessLevel(businessWorker);

        return account;
    }

    public static Account extractAccountFromModeratorChangeDataDto(ModeratorChangeDataDto moderatorChangeDataDto) {
        LocalDateTime now = LocalDateTime.now();

        Account account = new Account();

        setAccountChangeDataDtoFields(account, moderatorChangeDataDto, now);

        return account;
    }

    public static Account extractAccountFromAdministratorChangeDataDto(AdministratorChangeDataDto administratorChangeDataDto) {
        LocalDateTime now = LocalDateTime.now();

        Account account = new Account();

        setAccountChangeDataDtoFields(account, administratorChangeDataDto, now);

        return account;
    }


    public static AccessLevel getAccessLevel(Account from, AccessLevelType target) throws BaseAppException {
        Optional<AccessLevel> optionalAccessLevel = from.getAccessLevels().stream()
                .filter(accessLevel -> accessLevel.getAccessLevelType().equals(target)).findAny();

        return optionalAccessLevel.orElseThrow(() -> new AccountManagerException(ACCESS_LEVEL_DOES_NOT_EXIST_ERROR));
    }

    public static ClientDto toClientDto(Account account) throws BaseAppException {
        Client client = (Client) getAccessLevel(account, AccessLevelType.CLIENT);

        return new ClientDto(
                account.getLogin(),
                account.getFirstName(),
                account.getSecondName(),
                account.getEmail(),
                account.getLanguageType().getName(),
                toAddressDto(client.getHomeAddress()),
                client.getPhoneNumber(),
                account.getVersion());
    }

    public static BusinessWorkerDto toBusinessWorkerDto(BusinessWorker businessWorker) {

        Account account = businessWorker.getAccount();
        return new BusinessWorkerDto(
                account.getLogin(),
                account.getFirstName(),
                account.getSecondName(),
                account.getEmail(),
                account.getLanguageType().getName(),
                businessWorker.getPhoneNumber(),
                account.getVersion()
        );
    }

    public static BusinessWorkerWithCompanyDto toBusinessWorkerWithCompanyDto(BusinessWorker businessWorker) throws ETagException {

        Account account = businessWorker.getAccount();
        return new BusinessWorkerWithCompanyDto(
                account.getLogin(),
                account.getFirstName(),
                account.getSecondName(),
                account.getEmail(),
                account.getLanguageType().getName(),
                businessWorker.getPhoneNumber(),
                account.getVersion(),
                businessWorker.getCompany().getName(),
                businessWorker.getCompany().getPhoneNumber()

        );
    }

    /**
     * Mapuje oiekt klasy account na obiekt dto BusinessWorkerWithCompanyDto
     *
     * @param account konto użytkownika podawane konwersji
     * @return obiekt dto BusinessWorkerWithCompanyDto
     * @throws BaseAppException Bazowy wyjątek aplikacji
     */
    public static BusinessWorkerWithCompanyDto toBusinessWorkerWithCompanyDto(Account account) throws BaseAppException {
        BusinessWorker businessWorker = (BusinessWorker) getAccessLevel(account, AccessLevelType.BUSINESS_WORKER);
        return new BusinessWorkerWithCompanyDto(
                account.getLogin(),
                account.getFirstName(),
                account.getSecondName(),
                account.getEmail(),
                account.getLanguageType().getName(),
                businessWorker.getPhoneNumber(),
                businessWorker.getVersion(),
                businessWorker.getCompany().getName(),
                businessWorker.getCompany().getPhoneNumber()
        );
    }

    public static ModeratorDto toModeratorDto(Account account) {
        return new ModeratorDto(
                account.getLogin(),
                account.getFirstName(),
                account.getSecondName(),
                account.getEmail(),
                account.getLanguageType().getName(),
                account.getVersion()
        );
    }

    public static AdministratorDto toAdministratorDto(Account account) {
        return new AdministratorDto(
                account.getLogin(),
                account.getFirstName(),
                account.getSecondName(),
                account.getEmail(),
                account.getLanguageType().getName(),
                account.getVersion()
        );
    }

    public static AccountMetadataDto toAccountMetadataDto(Account account) {
        return new AccountMetadataDto(account.getCreationDateTime(),
                account.getLastAlterDateTime(),
                account.getCreatedBy().getLogin(),
                account.getAlteredBy().getLogin(),
                account.getAlterType().getName(),
                account.getVersion(),
                account.getLastIncorrectAuthenticationDateTime(),
                account.getLastIncorrectAuthenticationLogicalAddress(),
                account.getLastCorrectAuthenticationDateTime(),
                account.getLastCorrectAuthenticationLogicalAddress(),
                account.getNumberOfAuthenticationFailures(),
                account.getLanguageType().getName().toString());
    }
}
