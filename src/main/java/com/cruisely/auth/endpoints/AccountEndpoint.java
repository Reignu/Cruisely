package com.cruisely.auth.endpoints;

import lombok.extern.java.Log;
import org.apache.commons.codec.digest.DigestUtils;
import com.cruisely.common.I18n;
import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.BaseEndpoint;
import com.cruisely.common.mappers.MetadataMapper;
import com.cruisely.entities.common.wrappers.TokenWrapper;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.auth.Account;
import com.cruisely.entities.auth.Address;
import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.auth.accesslevels.Client;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.EndpointException;
import com.cruisely.exceptions.JWTException;
import com.cruisely.auth.dto.*;
import com.cruisely.auth.dto.changedata.*;
import com.cruisely.auth.dto.changes.ChangeAccessLevelStateDto;
import com.cruisely.auth.dto.changes.GrantAccessLevelDto;
import com.cruisely.auth.dto.detailsview.AccountDetailsViewDto;
import com.cruisely.auth.dto.registration.BusinessWorkerForRegistrationDto;
import com.cruisely.auth.dto.registration.ClientForRegistrationDto;
import com.cruisely.auth.endpoints.converters.AccountMapper;
import com.cruisely.auth.facades.TokenWrapperFacade;
import com.cruisely.auth.managers.AccountManagerLocal;
import com.cruisely.services.EmailService;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.cruisely.common.I18n.*;

/**
 * Class responsible for mapping DTO objects to model/entity objects related to user accounts and access levels,
 * and for invoking business logic methods with those mapped objects.
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(TrackingInterceptor.class)
@Log
public class AccountEndpoint extends BaseEndpoint implements AccountEndpointLocal {

    @Inject
    private AccountManagerLocal accountManager;

    @Inject
    private TokenWrapperFacade tokenWrapperFacade;

    @Inject
    I18n i18n;

    @PermitAll
    @Override
    public void createClientAccount(ClientForRegistrationDto clientForRegistrationDto) throws BaseAppException {
        Client client = AccountMapper.extractClientFromClientForRegistrationDto(clientForRegistrationDto);
        Account account = AccountMapper.extractAccountFromClientForRegistrationDto(clientForRegistrationDto);
        this.accountManager.createClientAccount(account, client);
    }

    @PermitAll
    @Override
    public void createBusinessWorkerAccount(BusinessWorkerForRegistrationDto businessWorkerForRegistrationDto) throws BaseAppException {
        BusinessWorker businessWorker = AccountMapper.extractBusinessWorkerFromBusinessWorkerForRegistrationDto(businessWorkerForRegistrationDto);
        Account account = AccountMapper.extractAccountFromBusinessWorkerForRegistrationDto(businessWorkerForRegistrationDto);
        this.accountManager.createBusinessWorkerAccount(account, businessWorker, businessWorkerForRegistrationDto.getCompanyName());
    }

    @RolesAllowed("grantAccessLevel")
    @Override
    public AccountDto grantAccessLevel(GrantAccessLevelDto grantAccessLevelDto) throws BaseAppException {
        if (grantAccessLevelDto.getAccessLevel().equals(AccessLevelType.MODERATOR)) {
            return AccountMapper.toAccountDto(
                    accountManager.grantModeratorAccessLevel(grantAccessLevelDto.getAccountLogin(), grantAccessLevelDto.getAccountVersion())
            );
        }

        if (grantAccessLevelDto.getAccessLevel().equals(AccessLevelType.ADMINISTRATOR)) {
            return AccountMapper.toAccountDto(
                    accountManager.grantAdministratorAccessLevel(grantAccessLevelDto.getAccountLogin(), grantAccessLevelDto.getAccountVersion())
            );
        }
        throw new EndpointException(ACCESS_LEVEL_NOT_ASSIGNABLE_ERROR);
    }

    @RolesAllowed("changeAccessLevelState")
    @Override
    public AccountDto changeAccessLevelState(ChangeAccessLevelStateDto changeAccessLevelStateDto) throws BaseAppException {
        return AccountMapper.toAccountDto(
                accountManager.changeAccessLevelState(changeAccessLevelStateDto.getAccountLogin(),
                        changeAccessLevelStateDto.getAccessLevel(), changeAccessLevelStateDto.isEnabled(),
                        changeAccessLevelStateDto.getAccountVersion())
        );
    }

    @RolesAllowed("getAccountDetailsByLogin")
    @Override
    public AccountDetailsViewDto getAccountDetailsByLogin(String login) throws BaseAppException {
        return AccountMapper.toAccountDetailsViewDto(accountManager.getAccountByLogin(login));
    }

    @RolesAllowed("selfGetAccountDetails")
    @Override
    public AccountDetailsViewDto getSelfDetails() throws BaseAppException {
        return AccountMapper.toAccountDetailsViewFilteringAccessLevels(accountManager.getCurrentUser());
    }

    @RolesAllowed("getAllAccounts")
    @Override
    public List<AccountDtoForList> getAllAccounts() throws BaseAppException {
        List<AccountDtoForList> res = new ArrayList<>();
        for (Account account : accountManager.getAllAccounts()) {
            res.add(AccountMapper.toAccountListDto(account));
        }
        return res;
    }

    @RolesAllowed("getAllUnconfirmedBusinessWorkers")
    @Override
    public List<BusinessWorkerWithCompanyDto> getAllUnconfirmedBusinessWorkers() throws BaseAppException {
        List<BusinessWorkerWithCompanyDto> res = new ArrayList<>();
        List<Account> accounts = accountManager.getAllUnconfirmedBusinessWorkers();
        for (Account account : accounts) {
            res.add(AccountMapper.toBusinessWorkerWithCompanyDto(account));
        }
        return res;
    }

    @RolesAllowed("blockUser")
    @Override
    public void blockUser(@NotNull(message = CONSTRAINT_NOT_NULL) String login, @NotNull(message = CONSTRAINT_NOT_NULL) long version) throws BaseAppException {
        Account account = this.accountManager.blockUser(login, version);

        Locale locale = new Locale(account.getLanguageType().getName().name());
        String body = i18n.getMessage(BLOCKED_ACCOUNT_BODY, locale);
        String subject = i18n.getMessage(BLOCKED_ACCOUNT_SUBJECT, locale);
        EmailService.sendEmailWithContent(account.getEmail().trim(), subject, body);
    }

    @PermitAll
    @Override
    public void requestPasswordReset(String login) throws BaseAppException {
        this.accountManager.requestPasswordReset(login);
    }

    @PermitAll
    @Override
    public void resetPassword(PasswordResetDto passwordResetDto) throws BaseAppException {
        TokenWrapper tokenWrapper = this.tokenWrapperFacade.findByToken(passwordResetDto.getToken());
        if (tokenWrapper.isUsed()) {
            throw new JWTException(PASSWORD_RESET_USED_TOKEN_ERROR);
        }
        this.accountManager.resetPassword(passwordResetDto.getLogin(), DigestUtils.sha256Hex(passwordResetDto.getPassword()), passwordResetDto.getToken());
    }

    @RolesAllowed("unblockUser")
    @Override
    public void unblockUser(@NotNull(message = CONSTRAINT_NOT_NULL) String unblockedUserLogin, @NotNull(message = CONSTRAINT_NOT_NULL) long version) throws BaseAppException {
        Account account = this.accountManager.unblockUser(unblockedUserLogin, version);

        Locale locale = new Locale(account.getLanguageType().getName().name());
        String body = i18n.getMessage(UNBLOCKED_ACCOUNT_BODY, locale);
        String subject = i18n.getMessage(UNBLOCKED_ACCOUNT_SUBJECT, locale);
        EmailService.sendEmailWithContent(account.getEmail().trim(), subject, body);
    }

    @RolesAllowed("requestSomeonesPasswordReset")
    @Override
    public void requestSomeonesPasswordReset(String login, String email) throws BaseAppException {
        this.accountManager.requestSomeonesPasswordReset(login, email);
    }

    @PermitAll
    @Override
    public void verifyAccount(AccountVerificationDto accountVerificationDto) throws BaseAppException {
        this.accountManager.verifyAccount(accountVerificationDto.getToken());
    }

    @RolesAllowed("changeOtherClientData")
    @Override
    public OtherClientChangeDataDto changeOtherClientData(OtherClientChangeDataDto otherClientChangeDataDto) throws BaseAppException {

        Address addr = new Address(otherClientChangeDataDto.getNewAddress().getNewHouseNumber(), otherClientChangeDataDto.getNewAddress().getNewStreet(), otherClientChangeDataDto.getNewAddress().getNewPostalCode(),
                otherClientChangeDataDto.getNewAddress().getNewCity(), otherClientChangeDataDto.getNewAddress().getNewCountry());

        return AccountMapper.accountDtoForClientDataChange(accountManager.changeOtherClientData(otherClientChangeDataDto.getLogin(), otherClientChangeDataDto.getNewPhoneNumber(), addr, otherClientChangeDataDto.getAccVersion()));
    }

    @RolesAllowed("changeOtherBusinessWorkerData")
    @Override
    public OtherBusinessWorkerChangeDataDto changeOtherBusinessWorkerData(OtherBusinessWorkerChangeDataDto otherBusinessWorkerChangeDataDto) throws BaseAppException {
        return AccountMapper.accountDtoForBusinnesWorkerDataChange(accountManager.changeOtherBusinessWorkerData(otherBusinessWorkerChangeDataDto.getLogin(), otherBusinessWorkerChangeDataDto.getNewPhoneNumber(), otherBusinessWorkerChangeDataDto.getAccVersion()));
    }

    @RolesAllowed("changeOtherAccountData")
    @Override
    public AccountDto changeOtherAccountData(OtherAccountChangeDataDto otherAccountChangeDataDto) throws BaseAppException {
        Account account = AccountMapper.extractAccountFromOtherAccountChangeDataDto(otherAccountChangeDataDto);
        return AccountMapper.toAccountDto(accountManager.changeOtherAccountData(account));
    }

    @RolesAllowed("changeEmail")
    @Override
    public void changeEmail(AccountVerificationDto accountVerificationDto) throws BaseAppException {
        accountManager.changeEmail(accountVerificationDto.getToken());
    }


    @RolesAllowed("changeClientData")
    @Override
    public void changeClientData(ClientChangeDataDto clientChangeDataDto) throws BaseAppException {
        Account account = AccountMapper.extractAccountFromClientChangeDataDto(clientChangeDataDto);
        accountManager.changeClientData(account);
    }

    @RolesAllowed("changeBusinessWorkerData")
    @Override
    public void changeBusinessWorkerData(BusinessWorkerChangeDataDto businessWorkerChangeDataDto) throws BaseAppException {
        Account account = AccountMapper.extractAccountFromBusinessWorkerChangeDataDto(businessWorkerChangeDataDto);
        accountManager.changeBusinessWorkerData(account);
    }

    @RolesAllowed("changeModeratorData")
    @Override
    public void changeModeratorData(ModeratorChangeDataDto moderatorChangeDataDto) throws BaseAppException {
        Account account = AccountMapper.extractAccountFromModeratorChangeDataDto(moderatorChangeDataDto);
        accountManager.changeModeratorData(account);
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public void changeLanguage(ChangeLanguageDto changeLanguageDto) throws BaseAppException {
        accountManager.changeLanguage(changeLanguageDto.getLogin(), changeLanguageDto.getVersion());
    }

    @RolesAllowed("changeAdministratorData")
    @Override
    public void changeAdministratorData(AdministratorChangeDataDto administratorChangeDataDto) throws BaseAppException {
        Account account = AccountMapper.extractAccountFromAdministratorChangeDataDto(administratorChangeDataDto);
        accountManager.changeAdministratorData(account);
    }

    @RolesAllowed("getAccountByLogin")
    @Override
    public AccountDto getAccountByLogin(String login) throws BaseAppException {
        return AccountMapper.toAccountDto(accountManager.getAccountByLogin(login));
    }

    @RolesAllowed("getAccessLevelByLogin")
    @Override
    public ClientDto getClientByLogin(String login) throws BaseAppException {
        return AccountMapper.toClientDto(accountManager.getAccountByLogin(login));
    }

    @RolesAllowed("getAccessLevelByLogin")
    @Override
    public BusinessWorkerDto getBusinessWorkerByLogin(String login) throws BaseAppException {
        return AccountMapper.toBusinessWorkerDto((BusinessWorker) accountManager.getAccountAccessLevel(login, AccessLevelType.BUSINESS_WORKER));
    }

    @RolesAllowed("getAccessLevelByLogin")
    @Override
    public ModeratorDto getModeratorByLogin(String login) throws BaseAppException {
        return AccountMapper.toModeratorDto(accountManager.getAccountByLogin(login));
    }

    @RolesAllowed("getAccessLevelByLogin")
    @Override
    public AdministratorDto getAdministratorByLogin(String login) throws BaseAppException {
        return AccountMapper.toAdministratorDto(accountManager.getAccountByLogin(login));
    }

    @RolesAllowed("authenticatedUser")
    public void changeOwnPassword(AccountChangeOwnPasswordDto accountChangeOwnPasswordDto) throws BaseAppException {
        this.accountManager.changeOwnPassword(accountChangeOwnPasswordDto.getLogin(), accountChangeOwnPasswordDto.getVersion(),
                accountChangeOwnPasswordDto.getOldPassword(), accountChangeOwnPasswordDto.getNewPassword());
    }

    @RolesAllowed("authenticatedUser")
    public void changeMode(ChangeModeDto changeModeDto) throws BaseAppException {
        this.accountManager.changeMode(changeModeDto.getLogin(), changeModeDto.isNewMode(), changeModeDto.getVersion());
    }

    @RolesAllowed("ConfirmBusinessWorker")
    @Override
    public void confirmBusinessWorker(BlockAccountDto blockAccountDto) throws BaseAppException {

        this.accountManager.confirmBusinessWorker(blockAccountDto.getLogin(), blockAccountDto.getVersion());
    }

    @RolesAllowed("changeEmail")
    @Override
    public void requestEmailChange(AccountChangeEmailDto accountChangeEmailDto) throws BaseAppException {
        accountManager.requestEmailChange(accountChangeEmailDto.getLogin(), accountChangeEmailDto.getNewEmail());
    }

    @RolesAllowed("changeOtherEmail")
    @Override
    public void requestOtherEmailChange(AccountChangeEmailDto accountChangeEmailDto) throws BaseAppException {
        accountManager.requestOtherEmailChange(accountChangeEmailDto.getLogin(), accountChangeEmailDto.getNewEmail());
    }

    @RolesAllowed("getMetadata")
    @Override
    public AccountMetadataDto getAccountMetadata(String login) throws BaseAppException {
        return AccountMapper.toAccountMetadataDto(accountManager.getAccountByLogin(login));
    }

    @RolesAllowed("getMetadata")
    @Override
    public MetadataDto getAccessLevelMetadata(String login, AccessLevelType accessLevelType) throws BaseAppException {
        return MetadataMapper.toMetadataDto(accountManager.getAccountAccessLevel(login, accessLevelType));
    }

    @RolesAllowed("getMetadata")
    @Override
    public MetadataDto getAddressMetadata(String login) throws BaseAppException {
        Client client = (Client) accountManager.getAccountAccessLevel(login, AccessLevelType.CLIENT);
        return MetadataMapper.toMetadataDto(client.getHomeAddress());
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public AccountMetadataDto getSelfMetadata() throws BaseAppException {
        return AccountMapper.toAccountMetadataDto(accountManager.getCurrentUser());
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public MetadataDto getSelfAccessLevelMetadata(AccessLevelType accessLevelType) throws BaseAppException {
        return MetadataMapper.toMetadataDto(accountManager.getCurrentUserAccessLevel(accessLevelType));
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public MetadataDto getSelfAddressMetadata() throws BaseAppException {
        Client client = (Client) accountManager.getCurrentUserAccessLevel(AccessLevelType.CLIENT);
        return MetadataMapper.toMetadataDto(client.getHomeAddress());
    }
}
