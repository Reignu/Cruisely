package com.cruisely.auth.managers;

import com.cruisely.entities.auth.*;
import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.auth.accesslevels.Client;
import com.cruisely.exceptions.BaseAppException;

import javax.ejb.Local;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class manages the business logic of accounts
 */
@Local
public interface AccountManagerLocal {

    /**
     * Retrieves the user object from the database
     *
     * @param login user's login
     * @return user entity object
     * @throws BaseAppException Base application exception, returned if the user is not found.
     */
    Account getAccountByLogin(String login) throws BaseAppException;

    /**
     * Creates an account assigning it the appropriate access level and address
     *
     * @param account account object
     * @param client  client access level object
     */
    void createClientAccount(Account account, Client client) throws BaseAppException;

    /**
     * Creates an account by assigning it the appropriate access level
     *
     * @param account        account object
     * @param businessWorker business worker access level object
     */
    void createBusinessWorkerAccount(Account account, BusinessWorker businessWorker, String companyName) throws BaseAppException;

    /**
     * Adds moderator access level to the user
     *
     * @param accountLogin   user's login
     * @param accountVersion account version at the time of changes
     * @return user entity object after changes
     * @throws BaseAppException Base application exception, returned when adding the level is impossible,
     *                          or violates the business rules of the application
     */
    Account grantModeratorAccessLevel(String accountLogin, long accountVersion) throws BaseAppException;

    /**
     * Adds administrator access level to the user
     *
     * @param accountLogin   user's login
     * @param accountVersion account version at the time of changes
     * @return user entity object after changes
     * @throws BaseAppException Base application exception, returned when adding the level is impossible,
     *                          or violates the business rules of the application
     */
    Account grantAdministratorAccessLevel(String accountLogin, long accountVersion) throws BaseAppException;


    /**
     * Changes the state of the user's access level (enables/disables)
     *
     * @param accountLogin   User's login
     * @param accessLevel    User's access level
     * @param enabled        boolean specifying the expected state of the access level
     * @param accountVersion Object version before calling the method
     * @return User object after changes
     * @throws BaseAppException Base application exception, returned when changing the access level state is impossible,
     *                          or violates the business rules of the application
     */
    Account changeAccessLevelState(String accountLogin, AccessLevelType accessLevel,
                                   boolean enabled, long accountVersion) throws BaseAppException;

    /**
     * Method responsible for blocking an account
     * <p>
     * Blocks the user with the given login
     *
     * @param login   User's login
     * @param version Object version to check
     * @throws BaseAppException Application exception thrown in case of an error in retrieving user data
     */
    Account blockUser(String login, long version) throws BaseAppException;


    /**
     * Method responsible for unblocking an account
     *
     * @param unblockedUserLogin login of the unblocked account
     * @param version            object version to check
     * @throws BaseAppException Base application exception, returned in case of an error in
     *                          data retrieval in the facade
     */
    Account unblockUser(String unblockedUserLogin, long version) throws BaseAppException;

    /**
     * Retrieves a list of accounts from the database
     *
     * @return list of accounts
     */
    List<Account> getAllAccounts() throws BaseAppException;


    /**
     * Method responsible for sending an email with a password reset link
     *
     * @param login user's login
     * @throws BaseAppException Base application exception, returned if the user with the given login does not exist, or if an exception was thrown by the EmailService class method while sending the email
     */
    void requestPasswordReset(String login) throws BaseAppException;

    /**
     * Method responsible for resetting the user's password.
     *
     * @param login        user's login
     * @param passwordHash hashed password
     * @param token        jwt token received by email
     * @throws BaseAppException Base application exception, returned if the token has expired or failed validation, and if the login in the token is different from the one sent explicitly in the dto, and in a situation where an optimistic lock exception was thrown
     */
    void resetPassword(String login, String passwordHash, String token) throws BaseAppException;

    /**
     * Method responsible for sending an email with a password reset link to the given email
     *
     * @param login user's login
     * @param email user's email
     * @throws BaseAppException Base application exception, returned if the user with the given login does not exist, or if an exception was thrown by the EmailService class method while sending the email
     */
    void requestSomeonesPasswordReset(String login, String email) throws BaseAppException;

    /**
     * Method responsible for verifying the user's account.
     *
     * @param token jwt token received by email
     * @throws BaseAppException Base application exception, returned if the token has expired or failed validation, and if the login or version is missing in the token, and if the account has already been activated, and in a situation where an optimistic lock exception was thrown
     */
    void verifyAccount(String token) throws BaseAppException;


    /**
     * Changes the data of the selected client
     *
     * @param login       client's login
     * @param phoneNumber changed phone number
     * @param addr        changed address
     * @param version     version
     * @return changed account
     * @throws BaseAppException Base application exception, returned if the token has expired or failed validation, and if the login or version is missing in the token, and if the account has already been activated, and in a situation where an optimistic lock exception was thrown
     */
    Account changeOtherClientData(String login, String phoneNumber, Address addr, long version) throws BaseAppException;

    /**
     * Changes the data of the selected company employee
     *
     * @param login       employee's login
     * @param phoneNumber new phone number
     * @param version     version
     * @return changed account
     * @throws BaseAppException Base application exception, returned if the token has expired or failed validation, and if the login or version is missing in the token, and if the account has already been activated, and in a situation where an optimistic lock exception was thrown
     */
    Account changeOtherBusinessWorkerData(String login, String phoneNumber, long version) throws BaseAppException;

    /**
     * Change the data of the selected moderator or administrator
     *
     * @param account account entity containing the changes
     * @return changed account
     */
    Account changeOtherAccountData(Account account) throws BaseAppException;


    /**
     * Changes the email of the account with the given login
     *
     * @param token token with authentication data
     */

    void changeEmail(String token) throws BaseAppException;

    /**
     * Change client data
     *
     * @param account account entity containing the changes
     */
    void changeClientData(Account account) throws BaseAppException;

    /**
     * Change company employee data
     *
     * @param account account entity containing the changes
     */
    void changeBusinessWorkerData(Account account) throws BaseAppException;

    /**
     * Change moderator data
     *
     * @param account account entity containing the changes
     */
    void changeModeratorData(Account account) throws BaseAppException;

    /**
     * Change administrator data
     *
     * @param account account entity containing the changes
     */
    void changeAdministratorData(Account account) throws BaseAppException;

    /**
     * Method responsible for editing fields in the database in case of incorrect login.
     *
     * @param login  User's login
     * @param IpAddr User's IP address
     * @param time   Time
     */

    void updateIncorrectAuthenticateInfo(String login, String IpAddr, LocalDateTime time) throws BaseAppException;

    /**
     * Method responsible for editing fields in the database in case of correct login.
     *
     * @param login  User's login
     * @param IpAddr User's IP address
     * @param time   Time
     * @return JWT Token
     */

    String updateCorrectAuthenticateInfo(String login, String IpAddr, LocalDateTime time) throws BaseAppException;

    /**
     * Method responsible for changing the current user's password
     *
     * @throws BaseAppException Base application exception thrown if the old password does not match the one from the database
     */
    void changeOwnPassword(String login, long version, String oldPassword, String newPassword) throws BaseAppException;

    /**
     * Method returning the active user
     * @return Active user's account
     * @throws BaseAppException Base application exception
     */
    Account getCurrentUser() throws BaseAppException;

    /**
     * Method responsible for changing the theme
     *
     * @param login   login of the user for whom the theme should be changed
     * @param newMode flag of the new theme
     * @param version version of the sent account
     * @throws BaseAppException Base application exception
     */
    void changeMode(String login, boolean newMode, long version) throws BaseAppException;

    /**
     * Method responsible for refreshing the JWT token
     *
     * @param token Current JWT token
     * @return token Renewed JWT token
     * @throws BaseAppException Base application exception, thrown if the passed token is not valid,
     *                          or its subject is not authorized for re-authentication
     */
    String refreshJWTToken(String token) throws BaseAppException;

    /**
     * Method responsible for retrieving all unconfirmed company employees
     *
     * @return list of account objects
     * @throws BaseAppException Base application exception
     */
    List<Account> getAllUnconfirmedBusinessWorkers() throws BaseAppException;

    /**
     * Method responsible for confirming a company employee
     *
     * @param login   login of the company employee
     * @param version version for optimistic locking
     * @throws BaseAppException base application exception
     */
    void confirmBusinessWorker(String login, long version) throws BaseAppException;

    /**
     * Method responsible for sending an email with a link confirming the email address change.
     *
     * @param login    user's login
     * @param newEmail new email address
     * @throws BaseAppException base application exception
     */
    void requestEmailChange(String login, String newEmail) throws BaseAppException;

    /**
     * Method responsible for sending an email with a link confirming the email address change by the administrator.
     *
     * @param login    user's login
     * @param newEmail new email address
     * @throws BaseAppException base application exception
     */
    void requestOtherEmailChange(String login, String newEmail) throws BaseAppException;


    /**
     * Method sends an email with an authentication code
     * @param login User's login
     * @throws BaseAppException Base application exception
     */
    void sendAuthenticationCodeEmail(String login, boolean darkMode, LanguageType languageType) throws BaseAppException;


    /**
     * Method responsible for verifying two-factor authentication (code) and editing fields in the database in case of correct login.
     *
     * @param login  User's login
     * @param code   two-factor authentication code
     * @param IpAddr User's IP address
     * @param time   Time
     * @return JWT Token
     * @throws BaseAppException base application exception
     */

    String authWCodeUpdateCorrectAuthenticateInfo(String login, String code, String IpAddr, LocalDateTime time) throws BaseAppException;

    /**
     * Returns the selected access level assigned to the account
     *
     * @param login           User's login
     * @param accessLevelType Type of the selected access level
     * @return Access level
     * @throws BaseAppException Base application exception in case of not finding the account or access level,
     *                          or in case of violation of business rules
     */
    AccessLevel getAccountAccessLevel(String login, AccessLevelType accessLevelType) throws BaseAppException;


    /**
     * Returns the selected access level assigned to the account
     *
     * @param accessLevelType Type of the selected access level
     * @return Access level
     * @throws BaseAppException Base application exception in case of not finding the account or access level,
     *                          or in case of violation of business rules
     */
    AccessLevel getCurrentUserAccessLevel(AccessLevelType accessLevelType) throws BaseAppException;

    /**
     * Changes user language
     *
     * @param login
     * @param version 
     * @throws BaseAppException 
     */
    void changeLanguage(String login, long version) throws BaseAppException;
}
