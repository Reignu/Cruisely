package com.cruisely.auth.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.TransactionalEndpoint;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.auth.dto.*;
import com.cruisely.auth.dto.changedata.*;
import com.cruisely.auth.dto.changes.ChangeAccessLevelStateDto;
import com.cruisely.auth.dto.changes.GrantAccessLevelDto;
import com.cruisely.auth.dto.detailsview.AccountDetailsViewDto;
import com.cruisely.auth.dto.registration.BusinessWorkerForRegistrationDto;
import com.cruisely.auth.dto.registration.ClientForRegistrationDto;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;

/**
 * Interface responsible for collecting mapped DTO objects into model/entity objects related to user accounts and access levels,
 * and for invoking business logic methods with those mapped objects.
 */
@Local
public interface AccountEndpointLocal extends TransactionalEndpoint {

    /**
     * Maps a registration DTO to model objects.
     *
     * @param clientForRegistrationDto DTO containing all required fields to create a new user account with the Client access level
     */
    void createClientAccount(ClientForRegistrationDto clientForRegistrationDto) throws BaseAppException;

    /**
     * Maps a registration DTO to model objects.
     *
     * @param businessWorkerForRegistrationDto DTO containing all required fields to create a new user account with the Business Worker access level
     */
    void createBusinessWorkerAccount(BusinessWorkerForRegistrationDto businessWorkerForRegistrationDto) throws BaseAppException;


    /**
     * Retrieves AccountDetailsViewDto for the specified user.
     *
     * @param login User login
     * @return AccountDetailsViewDto representing the user
     * @throws BaseAppException Base application exception thrown if the user is not found
     */
    AccountDetailsViewDto getAccountDetailsByLogin(String login) throws BaseAppException;

    /**
     * Retrieves AccountDetailsViewDto for the currently authenticated user.
     *
     * @return AccountDetailsViewDto representing the current user
     * @throws BaseAppException Base application exception thrown if the current user's account cannot be found
     */
    AccountDetailsViewDto getSelfDetails() throws BaseAppException;

    /**
     * Adds an access level to a user.
     *
     * @param grantAccessLevel DTO containing data needed to grant an access level
     * @return AccountDto representing the user after changes
     * @throws BaseAppException Base application exception thrown if granting the access level is not possible or violates business rules
     */
    AccountDto grantAccessLevel(GrantAccessLevelDto grantAccessLevel) throws BaseAppException;

    /**
     * Changes the state (enable/disable) of a user's access level.
     *
     * @param changeAccessLevelState DTO containing data needed to change the access level state
     * @return AccountDto representing the user after changes
     * @throws BaseAppException Base application exception thrown if the state change is not possible or violates business rules
     */
    AccountDto changeAccessLevelState(ChangeAccessLevelStateDto changeAccessLevelState) throws BaseAppException;


    /**
     * Retrieves all accounts as DTO transfer objects.
     *
     * @return List of AccountDtoForList objects
     */
    List<AccountDtoForList> getAllAccounts() throws BaseAppException;


    /**
     * Calls the method responsible for blocking a user account.
     *
     * @param login   Login of the user to block
     * @param version Account version for optimistic locking verification
     * @throws BaseAppException Application exception thrown when retrieving user data fails
     */
    void blockUser(@NotNull(message = CONSTRAINT_NOT_NULL) String login, @NotNull(message = CONSTRAINT_NOT_NULL) long version) throws BaseAppException;


    /**
     * Calls the method responsible for sending a password reset email with a reset link.
     *
     * @param login User login
     * @throws BaseAppException Base application exception thrown if the user does not exist or the EmailService throws an exception while sending the email
     */
    void requestPasswordReset(String login) throws BaseAppException;


    /**
     * Converts DTO to application model and calls the method responsible for resetting the user's password.
     *
     * @param passwordResetDto DTO representing data required for password reset
     * @throws BaseAppException Base application exception thrown if the token expired or failed validation, if the login in the token differs from the one provided in the DTO, or if an optimistic lock exception was thrown
     */
    void resetPassword(PasswordResetDto passwordResetDto) throws BaseAppException;


    /**
     * Calls the method responsible for unblocking a user account.
     *
     * @param unblockedUserLogin Login of the account to be unblocked
     * @param version            Account version for optimistic locking verification
     * @throws BaseAppException Base application exception thrown when retrieving user data fails
     */
    void unblockUser(@NotNull(message = CONSTRAINT_NOT_NULL) String unblockedUserLogin, @NotNull(message = CONSTRAINT_NOT_NULL) long version) throws BaseAppException;

    /**
     * Calls the method responsible for sending a password-reset email for a specified user and email address.
     *
     * @param login User login
     * @param email User email
     * @throws BaseAppException Base application exception thrown if the user does not exist or if the EmailService throws an exception while sending the email
     */
    void requestSomeonesPasswordReset(String login, String email) throws BaseAppException;

    /**
     * Verifies a user account using the provided verification DTO.
     *
     * @param accountVerificationDto DTO containing account verification information
     * @throws BaseAppException Base application exception
     */
    void verifyAccount(AccountVerificationDto accountVerificationDto) throws BaseAppException;

    /**
     * Changes data for the selected client.
     *
     * @param otherClientChangeDataDto DTO containing the changed client data
     */
    OtherClientChangeDataDto changeOtherClientData(OtherClientChangeDataDto otherClientChangeDataDto) throws BaseAppException;

    /**
     * Changes data for the selected business worker.
     *
     * @param otherBusinessWorkerChangeDataDto DTO containing the changed business worker data
     */
    OtherBusinessWorkerChangeDataDto changeOtherBusinessWorkerData(OtherBusinessWorkerChangeDataDto otherBusinessWorkerChangeDataDto) throws BaseAppException;

    /**
     * Changes data for the selected moderator or administrator.
     *
     * @param otherAccountChangeDataDto DTO containing the changed account data
     */
    AccountDto changeOtherAccountData(OtherAccountChangeDataDto otherAccountChangeDataDto) throws BaseAppException;

    /**
     * Maps a DTO containing a new email to the model object and updates the email address.
     *
     * @param accountVerificationDto DTO containing a verification token
     */
    void changeEmail(AccountVerificationDto accountVerificationDto) throws BaseAppException;

    /**
     * Zmienia dane clienta o podanym loginie
     *
     * @param clientChangeDataDto dto obiekt przechowujący informację o kliencie oraz zmienionych danych
     */
    void changeClientData(ClientChangeDataDto clientChangeDataDto) throws BaseAppException;

    /**
     * Zmienia dane pracownika o podanym loginie
     *
     * @param businessWorkerChangeDataDto dto obiekt przechowujący informację o procowniku oraz zmienionych danych
     */
    void changeBusinessWorkerData(BusinessWorkerChangeDataDto businessWorkerChangeDataDto) throws BaseAppException;

    /**
     * Zmienia dane moderatora o podanym loginie
     *
     * @param moderatorChangeDataDto dto obiekt przechowujący informację o moderatorze oraz zmienionych danych
     */
    void changeModeratorData(ModeratorChangeDataDto moderatorChangeDataDto) throws BaseAppException;

    /**
     * Metoda zmienia jezyk uzytkownika
     * @param changeLanguageDto Dto z loginem i wersją
     * @throws BaseAppException Bazowy wyjatek aplikacji
     */
    void changeLanguage(ChangeLanguageDto changeLanguageDto) throws BaseAppException;

    /**
     * Zmienia dane administratora o podanym loginie
     *
     * @param administratorChangeDataDto dto obiekt przechowujący informację o administratorze oraz zmienionych danych
     */
    void changeAdministratorData(AdministratorChangeDataDto administratorChangeDataDto) throws BaseAppException;

    /**
     * Zwraca konto o podanym loginie
     *
     * @param login login
     * @return konto
     */
    AccountDto getAccountByLogin(String login) throws BaseAppException;

    /**
     * Zwraca dto konta clienta o podanym loginie
     *
     * @param login login
     * @return dto konta clienta
     * @throws BaseAppException
     */
    ClientDto getClientByLogin(String login) throws BaseAppException;

    /**
     * Zwraca dto konta pracownika firmy o podanym loginie
     *
     * @param login login
     * @return dto konta pracownika firmy
     * @throws BaseAppException
     */
    BusinessWorkerDto getBusinessWorkerByLogin(String login) throws BaseAppException;

    /**
     * Zwraca dto konta moderatora o podanym loginie
     *
     * @param login login
     * @return dto konta moderatora
     * @throws BaseAppException
     */
    ModeratorDto getModeratorByLogin(String login) throws BaseAppException;

    /**
     * Zwraca dto konta administratora o podanym loginie
     *
     * @param login login
     * @return dto konta administratora
     * @throws BaseAppException
     */
    AdministratorDto getAdministratorByLogin(String login) throws BaseAppException;

    /**
     * Metoda odpowiedzialna za wywołanie metody odpowiedzialnej za zmianę hasła akutalnego użytkownika
     *
     * @param accountChangeOwnPasswordDto obiekt dto posiadający niezbedne dane do zmienienia hasła aktualnego użytkownika
     * @throws BaseAppException bazowy wyjątek aplikacji rzucany w przypadku gdy stare hasło podane przez użytkownika nie jest zgodne z tym w bazie danych
     */
    void changeOwnPassword(AccountChangeOwnPasswordDto accountChangeOwnPasswordDto) throws BaseAppException;

    /**
     * Metoda odpowiedzialna za zmianę jasnego-ciemnego motywu modułu prezentacji
     *
     * @param changeModeDto obiekt dto posiadający niezbędne dane do zmienienia motywu
     */

    void changeMode(ChangeModeDto changeModeDto) throws BaseAppException;

    /**
     * Metoda opdowiedzialna za pobranie wszystkich niezatwierdzonych pracowników firm
     *
     * @return lista obiektow dto BusinessWorkerWithCompanyDto
     * @throws BaseAppException Bazowy wyjątek aplikacji
     */
    List<BusinessWorkerWithCompanyDto> getAllUnconfirmedBusinessWorkers() throws BaseAppException;

    /**
     * Metoda odpowiedzialna za potwierdzenie pracownika firmy
     *
     * @param blockAccountDto obiekt dto posiadjący login oraz wersje pracownika firmy
     * @throws BaseAppException Bazowy wyjątek aplikacji
     */
    void confirmBusinessWorker(BlockAccountDto blockAccountDto) throws BaseAppException;

    /**
     * Metoda opdowiedzialna za zmiane maila
     *
     * @param accountChangeEmailDto obiekt dto posiadający login oraz nowy adres email
     * @throws BaseAppException Bazowy wyjątek aplikacji
     */
    void requestEmailChange(AccountChangeEmailDto accountChangeEmailDto) throws BaseAppException;

    /**
     * Metoda opdowiedzialna za wysłanie linku aktywacyjnego
     *
     * @param accountChangeEmailDto obiekt dto posiadający login oraz nowy adres email
     * @throws BaseAppException Bazowy wyjątek aplikacji
     */
    void requestOtherEmailChange(AccountChangeEmailDto
                                         accountChangeEmailDto) throws BaseAppException;

    /**
     * Metoda pobierające metadane dla danego użytkownika
     *
     * @param login Login użytkownika
     * @return Reprezentacja DTO metadanych
     * @throws BaseAppException Wyjątek bazowy aplikacji w przypadku niezlezienia konta lub naruszenia zasad biznesowych
     */
    AccountMetadataDto getAccountMetadata(String login) throws BaseAppException;

    /**
     * Metoda pobierające metadane dla danego poziomu dostepu użytkownika
     *
     * @param login           Login użytkownika
     * @param accessLevelType Wybrany poziom dostepu
     * @return Reprezentacja DTO metadanych
     * @throws BaseAppException Wyjątek bazowy aplikacji w przypadku niezlezienia konta lub poziomu dostępu,
     *                          lub naruszenia zasad biznesowych
     */
    MetadataDto getAccessLevelMetadata(String login, AccessLevelType accessLevelType) throws BaseAppException;

    /**
     * Metoda pobierające metadane adresu danego klienta
     *
     * @param login Login użytkownika
     * @return Reprezentacja DTO metadanych
     * @throws BaseAppException Wyjątek bazowy aplikacji w przypadku gdy konto nie zostanie znalezione,
     *                          użytkownik nie będzie klientem lub naruszone zostaną inne zasady biznesowe
     */
    MetadataDto getAddressMetadata(String login) throws BaseAppException;

    /**
     * Metoda pobierające metadane obecnego użytkownika
     *
     * @return Reprezentacja DTO metadanych
     * @throws BaseAppException Wyjątek bazowy aplikacji w przypadku niezlezienia konta lub naruszenia zasad biznesowych
     */
    AccountMetadataDto getSelfMetadata() throws BaseAppException;

    /**
     * Metoda pobierające metadane danego poziomu dostępu obecnego użytkownika
     *
     * @return Reprezentacja DTO metadanych
     * @throws BaseAppException Wyjątek bazowy aplikacji w przypadku niezlezienia konta lub poziomu dostępu,
     *                          lub naruszenia zasad biznesowych
     */
    MetadataDto getSelfAccessLevelMetadata(AccessLevelType accessLevelType) throws BaseAppException;

    /**
     * Metoda pobierające metadane adresu obecnie zalogowanego klienta
     *
     * @return Reprezentacja DTO metadanych
     * @throws BaseAppException Wyjątek bazowy aplikacji w przypadku gdy konto nie zostanie znalezione,
     *                          użytkownik nie będzie klientem lub naruszone zostaną inne zasady biznesowe
     */
    MetadataDto getSelfAddressMetadata() throws BaseAppException;

}

