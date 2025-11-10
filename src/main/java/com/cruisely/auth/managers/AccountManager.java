package com.cruisely.auth.managers;

import com.auth0.jwt.interfaces.Claim;
import lombok.extern.java.Log;
import org.apache.commons.codec.digest.DigestUtils;
import com.cruisely.common.I18n;
import com.cruisely.entities.common.wrappers.CodeWrapper;
import com.cruisely.entities.common.wrappers.TokenWrapper;
import com.cruisely.entities.auth.*;
import com.cruisely.entities.auth.accesslevels.Administrator;
import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.auth.accesslevels.Client;
import com.cruisely.entities.auth.accesslevels.Moderator;
import com.cruisely.exceptions.*;
import com.cruisely.auth.facades.AccountFacadeMok;
import com.cruisely.auth.facades.CodeWrapperFacade;
import com.cruisely.auth.facades.CompanyFacadeMok;
import com.cruisely.auth.facades.TokenWrapperFacade;
import com.cruisely.security.JWTHandler;
import com.cruisely.services.EmailService;
import com.cruisely.utils.PropertiesReader;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static com.cruisely.common.I18n.*;
import static com.cruisely.common.IntegrityUtils.checkForOptimisticLock;

/**
 * Class that manages account business logic
 */

@Log
@Stateful
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors(TrackingInterceptor.class)
public class AccountManager extends BaseManagerMok implements AccountManagerLocal {

    @Inject
    private AccountFacadeMok accountFacade;

    @Inject
    private CompanyFacadeMok companyFacadeMok;

    @Inject
    private TokenWrapperFacade tokenWrapperFacade;

    @Inject
    private CodeWrapperFacade codeWrapperFacade;

    @Inject
    I18n i18n;

    private static final Properties securityProperties = PropertiesReader.getSecurityProperties();

    @PermitAll
    @Override
    public void createClientAccount(Account account, Client client) throws BaseAppException {
        account.setLanguageType(accountFacade.getLanguageTypeWrapperByLanguageType(account.getLanguageType().getName()));
        setAccessLevelInitialMetadata(account, client, true);
        Address address = client.getHomeAddress();

        setCreatedMetadata(account, account, address);
        accountFacade.create(account);

        if (accountFacade.findByLogin(account.getLogin()) != null) {
            sendVerificationEmail(account);
        }
    }

    @PermitAll
    @Override
    public void createBusinessWorkerAccount(Account account, BusinessWorker businessWorker, String companyName) throws BaseAppException {
        account.setLanguageType(accountFacade.getLanguageTypeWrapperByLanguageType(account.getLanguageType().getName()));
        setAccessLevelInitialMetadata(account, businessWorker, true);
        businessWorker.setCompany(companyFacadeMok.getCompanyByName(companyName));

        setCreatedMetadata(account, account);
        accountFacade.create(account);

        if (accountFacade.findByLogin(account.getLogin()) != null) {
            sendVerificationEmail(account);
        }
    }

    @RolesAllowed("grantAccessLevel")
    @Override
    public Account grantModeratorAccessLevel(String accountLogin, long accountVersion) throws BaseAppException {
        Account account = accountFacade.findByLogin(accountLogin);
        checkForOptimisticLock(account, accountVersion);

        if (account.getAccessLevels().stream().anyMatch(accessLevel -> accessLevel.getAccessLevelType() == AccessLevelType.MODERATOR)) {
            throw new AccountManagerException(ACCESS_LEVEL_ALREADY_ASSIGNED_ERROR);
        }

        Moderator moderator = new Moderator(true);
        setAccessLevelInitialMetadata(account, moderator, false);
        setUpdatedMetadata(account);
        sendMail(account, ACCESS_LEVEL_GRANT_MODERATOR_SUBJECT, ACCESS_LEVEL_GRANT_MODERATOR_BODY);
        return account;
    }

    private void sendMail(Account account, String subjectString, String bodyString) throws EmailServiceException {
        Locale locale = new Locale(account.getLanguageType().getName().name());
        String subject = i18n.getMessage(subjectString, locale);
        String body = i18n.getMessage(bodyString, locale);
        EmailService.sendEmailWithContent(account.getEmail().trim(), subject, body);

    }

    @RolesAllowed("grantAccessLevel")
    @Override
    public Account grantAdministratorAccessLevel(String accountLogin, long accountVersion) throws BaseAppException {
        Account account = accountFacade.findByLogin(accountLogin);
        checkForOptimisticLock(account, accountVersion);

        if (account.getAccessLevels().stream().anyMatch(accessLevel -> accessLevel.getAccessLevelType() == AccessLevelType.ADMINISTRATOR)) {
            throw new AccountManagerException(ACCESS_LEVEL_ALREADY_ASSIGNED_ERROR);
        }

        Administrator administrator = new Administrator(true);
        setAccessLevelInitialMetadata(account, administrator, false);
        setUpdatedMetadata(account);
        sendMail(account, ACCESS_LEVEL_GRANT_ADMINISTRATOR_SUBJECT, ACCESS_LEVEL_GRANT_ADMINISTRATOR_BODY);
        return account;
    }

    @RolesAllowed("changeAccessLevelState")
    @Override
    public Account changeAccessLevelState(String accountLogin, AccessLevelType accessLevelType,
                                          boolean enabled, long accountVersion) throws BaseAppException {
        Account account = accountFacade.findByLogin(accountLogin);
        checkForOptimisticLock(account, accountVersion);

        Optional<AccessLevel> accountAccessLevel = account.getAccessLevels().stream()
                .filter(accessLevel -> accessLevel.getAccessLevelType() == accessLevelType).findFirst();

        if (accountAccessLevel.isEmpty()) {
            throw new AccountManagerException(ACCESS_LEVEL_NOT_ASSIGNED_ERROR);
        }

        AccessLevel accessLevel = accountAccessLevel.get();

        if (enabled == accessLevel.isEnabled()) {
            throw new AccountManagerException(enabled ? ACCESS_LEVEL_ALREADY_ENABLED_ERROR : ACCESS_LEVEL_ALREADY_DISABLED_ERROR);
        }

        accessLevel.setEnabled(enabled);
        setUpdatedMetadata(account, accessLevel);
        switch (accountAccessLevel.get().getAccessLevelType()) {
            case ADMINISTRATOR: {
                if (enabled) {
                    sendMail(account, ACCESS_LEVEL_REACTIVATE_ADMINISTRATOR_SUBJECT, ACCESS_LEVEL_REACTIVATE_ADMINISTRATOR_BODY);
                } else {
                    sendMail(account, ACCESS_LEVEL_DEACTIVATE_ADMINISTRATOR_SUBJECT, ACCESS_LEVEL_DEACTIVATE_ADMINISTRATOR_BODY);
                }
                break;
            }
            case MODERATOR: {
                if (enabled) {
                    sendMail(account, ACCESS_LEVEL_REACTIVATE_MODERATOR_SUBJECT, ACCESS_LEVEL_REACTIVATE_MODERATOR_BODY);
                } else {
                    sendMail(account, ACCESS_LEVEL_DEACTIVATE_MODERATOR_SUBJECT, ACCESS_LEVEL_DEACTIVATE_MODERATOR_BODY);
                }
                break;
            }
            case BUSINESS_WORKER: {
                if (enabled) {
                    sendMail(account, ACCESS_LEVEL_REACTIVATE_BUSINESS_WORKER_SUBJECT, ACCESS_LEVEL_REACTIVATE_BUSINESS_WORKER_BODY);
                } else {
                    sendMail(account, ACCESS_LEVEL_DEACTIVATE_BUSINESS_WORKER_SUBJECT, ACCESS_LEVEL_DEACTIVATE_BUSINESS_WORKER_BODY);
                }
                break;
            }
            case CLIENT: {
                if (enabled) {
                    sendMail(account, ACCESS_LEVEL_REACTIVATE_CLIENT_SUBJECT, ACCESS_LEVEL_REACTIVATE_CLIENT_BODY);
                } else {
                    sendMail(account, ACCESS_LEVEL_DEACTIVATE_CLIENT_SUBJECT, ACCESS_LEVEL_DEACTIVATE_CLIENT_BODY);
                }
                break;
            }

        }
        return account;
    }


    private void setAccessLevelInitialMetadata(Account account, AccessLevel accessLevel, boolean newAccount) throws BaseAppException {
        setCreatedMetadata(newAccount ? account : getCurrentUser(), accessLevel);
        account.setAccessLevel(accessLevel);
        accessLevel.setAccount(account);
    }

    private void sendVerificationEmail(Account account) throws BaseAppException {
        Map<String, Object> claims = Map.of("version", account.getVersion());
        String token = JWTHandler.createTokenEmail(claims, account.getLogin());
        Locale locale = new Locale(account.getLanguageType().getName().name());
        String subject = i18n.getMessage(VERIFICATION_EMAIL_SUBJECT, locale);
        String body = i18n.getMessage(VERIFICATION_EMAIL_BODY, locale);
        TokenWrapper tokenWrapper = TokenWrapper.builder().token(token).account(account).used(false).build();
        this.tokenWrapperFacade.create(tokenWrapper);
        String contentHtml = "<a href=\"" + PropertiesReader.getSecurityProperties().getProperty("app.baseurl") + "/verify/accountVerification/" + token + "\">" + body + "</a>";
        EmailService.sendEmailWithContent(account.getEmail().trim(), subject, contentHtml);
    }

    @RolesAllowed("getAllAccounts")
    @Override
    public List<Account> getAllAccounts() throws BaseAppException {
        Account currentUser = getCurrentUser();
        return accountFacade.findAll().stream().filter(acc -> !acc.getLogin().equals(currentUser.getLogin())).collect(Collectors.toList());
    }

    @RolesAllowed("getAllUnconfirmedBusinessWorkers")
    @Override
    public List<Account> getAllUnconfirmedBusinessWorkers() {
        return accountFacade.getUnconfirmedBusinessWorkers().stream().map(AccessLevel::getAccount).collect(Collectors.toList());
    }

    @RolesAllowed("blockUser")
    @Override
    public Account blockUser(String login, long version) throws BaseAppException {
        Account account = this.accountFacade.findByLogin(login);
        checkForOptimisticLock(account, version);

        account.setActive(false);
        setUpdatedMetadata(account);
        accountFacade.edit(account);
        return account;
    }


    @PermitAll
    @Override
    public void requestPasswordReset(String login) throws BaseAppException {
        Account account = this.accountFacade.findByLogin(login);
        if (!account.isConfirmed()) {
            throw new AccountManagerException(ACCOUNT_NOT_VERIFIED_ERROR);
        }
        if (!account.isActive()) {
            throw new AccountManagerException(ACCOUNT_NOT_ACTIVE_ERROR);
        }
        Map<String, Object> claims = Map.of("version", account.getVersion());
        String token = JWTHandler.createToken(claims, login);
        TokenWrapper tokenWrapper = TokenWrapper.builder().token(token).account(account).used(false).build();
        this.tokenWrapperFacade.create(tokenWrapper);
        Locale locale = new Locale(account.getLanguageType().getName().name());
        String subject = i18n.getMessage(REQUESTED_PASSWORD_RESET_SUBJECT, locale);
        String body = i18n.getMessage(REQUESTED_PASSWORD_RESET_BODY, locale);
        String contentHtml = "<a href=\"" + PropertiesReader.getSecurityProperties().getProperty("app.baseurl") + "/reset/passwordReset/" + token + "\">" + body + "</a>";
        EmailService.sendEmailWithContent(account.getEmail().trim(), subject, contentHtml);
    }

    @RolesAllowed("changeEmail")
    @Override
    public void requestEmailChange(String login, String newEmail) throws BaseAppException {
        Account account = this.accountFacade.findByLogin(login);
        if (this.accountFacade.isEmailPresent(newEmail)) {
            throw new AccountManagerException(EMAIL_RESERVED_ERROR);
        }
        Map<String, Object> claims = Map.of("version", account.getVersion(), "email", newEmail, "accessLevels", account.getAccessLevels()
                .stream().map(accessLevel -> accessLevel.getAccessLevelType().name()).collect(Collectors.toList()));

        String token = JWTHandler.createToken(claims, login);
        TokenWrapper tokenWrapper = TokenWrapper.builder().token(token).account(account).used(false).build();
        this.tokenWrapperFacade.create(tokenWrapper);
        Locale locale = new Locale(account.getLanguageType().getName().name());
        String subject = i18n.getMessage(REQUEST_EMAIL_CHANGE_SUBJECT, locale);
        String body = i18n.getMessage(REQUEST_EMAIL_CHANGE_BODY, locale);
        String contentHtml = "<a href=\"" + PropertiesReader.getSecurityProperties().getProperty("app.baseurl") + "/reset/changeEmail/" + token + "\">" + body + "</a>";
        EmailService.sendEmailWithContent(account.getEmail().trim(), subject, contentHtml);
    }

    @RolesAllowed("changeOtherEmail")
    @Override
    public void requestOtherEmailChange(String login, String newEmail) throws BaseAppException {
        Account account = this.accountFacade.findByLogin(login);
        if (this.accountFacade.isEmailPresent(newEmail)) {
            throw new AccountManagerException(EMAIL_RESERVED_ERROR);
        }
        Map<String, Object> claims = Map.of("version", account.getVersion(), "email", newEmail, "accessLevels", account.getAccessLevels()
                .stream().map(accessLevel -> accessLevel.getAccessLevelType().name()).collect(Collectors.toList()));

        String token = JWTHandler.createToken(claims, login);
        TokenWrapper tokenWrapper = TokenWrapper.builder().token(token).account(account).used(false).build();
        this.tokenWrapperFacade.create(tokenWrapper);
        Locale locale = new Locale(account.getLanguageType().getName().name());
        String subject = i18n.getMessage(REQUEST_EMAIL_CHANGE_SUBJECT, locale);
        String body = i18n.getMessage(REQUEST_EMAIL_CHANGE_BODY, locale);
        String contentHtml = "<a href=\"" + PropertiesReader.getSecurityProperties().getProperty("app.baseurl") + "/reset/changeEmail/" + token + "\">" + body + "</a>";
        EmailService.sendEmailWithContent(account.getEmail().trim(), subject, contentHtml);
    }


    private TokenWrapper validateToken(String token) throws AccountManagerException, JWTException {
        TokenWrapper tokenW;
        try {
            tokenW = this.tokenWrapperFacade.findByToken(token);
        } catch (BaseAppException e) {
            if (e.getMessage().equals(NO_SUCH_ELEMENT_ERROR)) {
                throw new AccountManagerException(TOKEN_INVALIDATE_ERROR);
            } else {
                throw new AccountManagerException(e.getMessage(), e);
            }
        }
        if (tokenW.isUsed()) {
            throw new AccountManagerException(TOKEN_ALREADY_USED_ERROR);
        }

        JWTHandler.validateToken(token);

        return tokenW;
    }

    @PermitAll
    @Override
    public void resetPassword(String login, String passwordHash, String token) throws BaseAppException {

        validateToken(token);
        Map<String, Claim> claims = JWTHandler.getClaimsFromToken(token);
        if (claims.get("sub").isNull() || claims.get("version").isNull()) {
            throw new AccountManagerException(PASSWORD_RESET_TOKEN_CONTENT_ERROR);
        }

        if (!claims.get("sub").asString().equals(login)) {
            throw new AccountManagerException(PASSWORD_RESET_IDENTITY_ERROR);
        }
        Account account = this.accountFacade.findByLogin(login);

        if (!account.isConfirmed()) {
            throw new AccountManagerException(ACCOUNT_NOT_VERIFIED_ERROR);
        }

        checkForOptimisticLock(account, claims.get("version").asLong());

        account.setPasswordHash(passwordHash);

        setUpdatedMetadataWithModifier(account, account);
        this.accountFacade.edit(account);

        TokenWrapper tokenWrapper = this.tokenWrapperFacade.findByToken(token);
        tokenWrapper.setUsed(true);
        this.tokenWrapperFacade.edit(tokenWrapper);

    }

    @PermitAll
    @Override
    public void verifyAccount(String token) throws BaseAppException {

        TokenWrapper tokenW = validateToken(token);
        Map<String, Claim> claims = JWTHandler.getClaimsFromToken(token);
        Date expire = JWTHandler.getExpirationTimeFromToken(token);

        if (claims.get("sub").isNull() || claims.get("version").isNull()) {
            throw new AccountManagerException(ACCOUNT_VERIFICATION_TOKEN_CONTENT_ERROR);
        }

        String login = claims.get("sub").asString();
        if (expire.before(new Date())) {
            throw new AccountManagerException(TOKEN_EXPIRED_ERROR);
        }

        Account account = this.accountFacade.findByLogin(login);
        if (account.isConfirmed()) {
            throw new AccountManagerException(ACCOUNT_VERIFICATION_TOKEN_ALREADY_VERIFIED_ERROR);
        }


        account.setConfirmed(true);
        tokenW.setUsed(true);
        this.tokenWrapperFacade.edit(tokenW);
        setUpdatedMetadataWithModifier(account, account);
        accountFacade.edit(account);
        sendMail(account, ACTIVATE_ACCOUNT_SUBJECT, ACTIVATE_ACCOUNT_BODY);
    }

    @RolesAllowed("requestSomeonesPasswordReset")
    @Override
    public void requestSomeonesPasswordReset(String login, String email) throws BaseAppException {
        Account account = this.accountFacade.findByLogin(login);
        if (!account.isConfirmed()) {
            throw new AccountManagerException(ACCOUNT_NOT_VERIFIED_ERROR);
        }
        if (!account.isActive()) {
            throw new AccountManagerException(ACCOUNT_NOT_ACTIVE_ERROR);
        }
        Map<String, Object> claims = Map.of("version", account.getVersion());
        Locale locale = new Locale(account.getLanguageType().getName().name());
        String token = JWTHandler.createToken(claims, login);
        String subject = i18n.getMessage(REQUESTED_PASSWORD_RESET_SUBJECT, locale);
        String body = i18n.getMessage(REQUESTED_PASSWORD_RESET_BODY, locale);
        TokenWrapper tokenWrapper = TokenWrapper.builder().token(token).account(account).used(false).build();
        this.tokenWrapperFacade.create(tokenWrapper);
        String contentHtml = "<a href=\"" + PropertiesReader.getSecurityProperties().getProperty("app.baseurl") + "/reset/passwordReset/" + token + "\">" + body + "</a>";
        EmailService.sendEmailWithContent(email, subject, contentHtml);
    }

    @RolesAllowed("unblockUser")
    @Override
    public Account unblockUser(String unblockedUserLogin, long version) throws BaseAppException {
        Account account = this.accountFacade.findByLogin(unblockedUserLogin);
        checkForOptimisticLock(account, version);

        account.setActive(true);
        setUpdatedMetadata(account);
        return account;
    }

    @RolesAllowed("changeOtherClientData")
    @Override
    public Account changeOtherClientData(String login, String phoneNumber, Address addr, long version) throws BaseAppException {
        Account targetAccount = accountFacade.findByLogin(login);
        Client targetClient = (Client) getAccessLevel(targetAccount, AccessLevelType.CLIENT);
        checkForOptimisticLock(targetClient, version);

        targetClient.setPhoneNumber(phoneNumber);
        Address targetAddress = targetClient.getHomeAddress();
        targetAddress.setHouseNumber(addr.getHouseNumber());
        targetAddress.setStreet(addr.getStreet());
        targetAddress.setPostalCode(addr.getPostalCode());
        targetAddress.setCity(addr.getCity());
        targetAddress.setCountry(addr.getCountry());
        setUpdatedMetadata(targetClient, targetAddress);
        return targetAccount;
    }

    private void updateClient(Client fromClient, Client targetClient) {
        targetClient.setPhoneNumber(fromClient.getPhoneNumber());

        Address targetAddress = targetClient.getHomeAddress();
        Address fromAddress = fromClient.getHomeAddress();
        targetAddress.setHouseNumber(fromAddress.getHouseNumber());
        targetAddress.setStreet(fromAddress.getStreet());
        targetAddress.setPostalCode(fromAddress.getPostalCode());
        targetAddress.setCity(fromAddress.getCity());
        targetAddress.setCountry(fromAddress.getCountry());
    }

    @RolesAllowed("changeOtherAccountData")
    public Account changeOtherAccountData(Account updatedAccount) throws BaseAppException {
        Account targetAccount = accountFacade.findByLogin(updatedAccount.getLogin());
        checkForOptimisticLock(targetAccount, updatedAccount.getVersion());

        targetAccount.setFirstName(updatedAccount.getFirstName());
        targetAccount.setSecondName(updatedAccount.getSecondName());

        setUpdatedMetadata(targetAccount);
        return targetAccount;
    }

    @RolesAllowed("changeOtherBusinessWorkerData")
    @Override
    public Account changeOtherBusinessWorkerData(String login, String phoneNumber, long version) throws BaseAppException {
        Account targetAccount = accountFacade.findByLogin(login);
        BusinessWorker targetBusinessWorker = (BusinessWorker) getAccessLevel(targetAccount, AccessLevelType.BUSINESS_WORKER);
        checkForOptimisticLock(targetBusinessWorker, version);

        targetBusinessWorker.setPhoneNumber(phoneNumber);
        setUpdatedMetadata(targetBusinessWorker);
        return targetAccount;
    }


    @RolesAllowed("changeEmail")
    @Override
    public void changeEmail(String token) throws BaseAppException {
        validateToken(token);

        Map<String, Claim> claims = JWTHandler.getClaimsFromToken(token);
        Date expire = JWTHandler.getExpirationTimeFromToken(token);

        if (claims.get("sub").isNull() || claims.get("version").isNull()) {
            throw new AccountManagerException(ACCOUNT_VERIFICATION_TOKEN_CONTENT_ERROR);
        }

        String login = claims.get("sub").asString();
        if (expire.before(new Date())) {
            throw new AccountManagerException(TOKEN_EXPIRED_ERROR);
        }

        Account account = this.accountFacade.findByLogin(login);
        checkForOptimisticLock(account, claims.get("version").asLong());


        String newEmail = claims.get("email").asString();
        if (this.accountFacade.isEmailPresent(newEmail)) {
            throw new AccountManagerException(EMAIL_RESERVED_ERROR);
        }
        TokenWrapper tokenWrapper = this.tokenWrapperFacade.findByToken(token);
        tokenWrapper.setUsed(true);
        this.tokenWrapperFacade.edit(tokenWrapper);
        account.setEmail(newEmail.toLowerCase());

        setUpdatedMetadata(account);
    }

    @RolesAllowed("getMetadata")
    @Override
    public AccessLevel getAccountAccessLevel(String login, AccessLevelType accessLevelType) throws BaseAppException {
        Account account = accountFacade.findByLogin(login);
        return getAccessLevel(account, accessLevelType);
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public AccessLevel getCurrentUserAccessLevel(AccessLevelType accessLevelType) throws BaseAppException {
        Account account = getCurrentUser();
        return getAccessLevel(account, accessLevelType);
    }


    private AccessLevel getAccessLevel(Account account, AccessLevelType accessLevelType) throws AccountManagerException {
        Optional<AccessLevel> optionalAccessLevel = account.getAccessLevels().stream()
                .filter(accessLevel -> accessLevel.getAccessLevelType().equals(accessLevelType)).findAny();

        return optionalAccessLevel.orElseThrow(() -> new AccountManagerException(ACCESS_LEVEL_DOES_NOT_EXIST_ERROR));
    }

    private void setAccountChanges(Account target, Account from) throws BaseAppException {
        checkForOptimisticLock(target, from.getVersion());

        target.setFirstName(from.getFirstName());
        target.setSecondName(from.getSecondName());
    }


    @RolesAllowed("changeClientData")
    @Override
    public void changeClientData(Account fromAccount) throws BaseAppException {
        Account targetAccount = accountFacade.findByLogin(fromAccount.getLogin());
        checkForOptimisticLock(fromAccount, targetAccount.getVersion());

        setAccountChanges(targetAccount, fromAccount);

        Client targetClient = (Client) getAccessLevel(targetAccount, AccessLevelType.CLIENT);

        Client fromClient = (Client) getAccessLevel(fromAccount, AccessLevelType.CLIENT);
        updateClient(fromClient, targetClient);

        Address targetAddress = targetClient.getHomeAddress();

        setUpdatedMetadata(targetClient, targetAddress, targetAccount);
    }

    @RolesAllowed("changeBusinessWorkerData")
    @Override
    public void changeBusinessWorkerData(Account fromAccount) throws BaseAppException {
        Account targetAccount = accountFacade.findByLogin(fromAccount.getLogin());
        checkForOptimisticLock(fromAccount, targetAccount.getVersion());

        setAccountChanges(targetAccount, fromAccount);

        BusinessWorker targetBusinessWorker = (BusinessWorker) getAccessLevel(targetAccount, AccessLevelType.BUSINESS_WORKER);

        BusinessWorker fromBusinessWorker = (BusinessWorker) getAccessLevel(fromAccount, AccessLevelType.BUSINESS_WORKER);
        targetBusinessWorker.setPhoneNumber(fromBusinessWorker.getPhoneNumber());
        setUpdatedMetadata(targetBusinessWorker, targetAccount);
    }

    @PermitAll
    @Override
    public void updateIncorrectAuthenticateInfo(String login, String IpAddr, LocalDateTime time) throws BaseAppException {

        Account account = updateAuthenticateInfo(login, IpAddr, time, false);
        if (account.getNumberOfAuthenticationFailures() >= Long.parseLong(securityProperties.getProperty("max.incorrect.logins"))) {
            account.setActive(false);
            accountFacade.edit(account);
            sendMail(account, BLOCKED_ACCOUNT_SUBJECT, BLOCKED_ACCOUNT_BODY);
        }
    }

    @RolesAllowed("changeModeratorData")
    @Override
    public void changeModeratorData(Account fromAccount) throws BaseAppException {
        Account targetAccount = accountFacade.findByLogin(fromAccount.getLogin());
        checkForOptimisticLock(fromAccount, targetAccount.getVersion());

        setAccountChanges(targetAccount, fromAccount);
        Moderator targetModerator = (Moderator) getAccessLevel(targetAccount, AccessLevelType.MODERATOR);
        setUpdatedMetadata(targetModerator, targetAccount);
    }

    @RolesAllowed("changeAdministratorData")
    @Override
    public void changeAdministratorData(Account fromAccount) throws BaseAppException {
        Account targetAccount = accountFacade.findByLogin(fromAccount.getLogin());
        checkForOptimisticLock(fromAccount, targetAccount.getVersion());

        setAccountChanges(targetAccount, fromAccount);

        Administrator targetAdministrator = (Administrator) getAccessLevel(targetAccount, AccessLevelType.ADMINISTRATOR);

        setUpdatedMetadata(targetAdministrator, targetAccount);
    }


    @RolesAllowed({"getAccountByLogin", "getAccountDetailsByLogin", "selfGetAccountDetails", "getAccessLevelByLogin"})
    @Override
    public Account getAccountByLogin(String login) throws BaseAppException {
        return accountFacade.findByLogin(login);
    }

    @PermitAll
    @Override
    public String updateCorrectAuthenticateInfo(String login, String ipAddr, LocalDateTime time) throws BaseAppException {
        Account account = updateAuthenticateInfo(login, ipAddr, time, true);
        account.setNumberOfAuthenticationFailures(0);
        setUpdatedMetadataWithModifier(account, account);

        Map<String, Object> map = Map.of("accessLevels", getAuthorizedAccessLevels(account)
                .map(accessLevel -> accessLevel.getAccessLevelType().name()).collect(Collectors.toList()));

        if (account.getAccessLevels().stream().anyMatch(accessLevel -> accessLevel.getAccessLevelType() == AccessLevelType.ADMINISTRATOR && accessLevel.isEnabled())) {
            sendMail(account, LOG_IN_SUBJECT, LOG_IN_BODY);
        }
        return JWTHandler.createToken(map, account.getLogin());
    }


    @RolesAllowed("authenticatedUser")
    public String refreshJWTToken(String token) throws BaseAppException {
        Map<String, Claim> claims = JWTHandler.getClaimsFromToken(token);

        try {
            String login = claims.get("sub").asString();
            Set<String> accessLevelsFromToken = new HashSet<>(claims.get("accessLevels").asList(String.class));
            Account account = accountFacade.findByLogin(login);
            if (!canUserRefreshToken(account, accessLevelsFromToken)) {
                throw new AccountManagerException(TOKEN_REFRESH_ERROR);
            }
            return JWTHandler.refreshToken(token);
        } catch (NullPointerException e) {
            throw new AccountManagerException(TOKEN_REFRESH_ERROR);
        }
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public void changeOwnPassword(String login, long version, String oldPassword, String newPassword) throws BaseAppException {
        Account account = accountFacade.findByLogin(login);
        checkForOptimisticLock(account, version);

        if (!account.getPasswordHash().equals(DigestUtils.sha256Hex(oldPassword))) {
            throw new AccountManagerException(PASSWORDS_DONT_MATCH_ERROR);
        }

        if (account.getPasswordHash().equals(DigestUtils.sha256Hex(newPassword))) {
            throw new AccountManagerException(PASSWORDS_ARE_THE_SAME_ERROR);
        }

        account.setPasswordHash(DigestUtils.sha256Hex(newPassword));
        setUpdatedMetadata(account);
    }

    @RolesAllowed("ConfirmBusinessWorker")
    @Override
    public void confirmBusinessWorker(String login, long version) throws BaseAppException {
        Account account = accountFacade.findByLogin(login);
        BusinessWorker worker = (BusinessWorker) getAccessLevel(account, AccessLevelType.BUSINESS_WORKER);
        checkForOptimisticLock(worker, version);

        if (worker.isConfirmed()) {
            throw new AccountManagerException(BUSINESS_WORKER_CONFIRMED);
        }
        worker.setConfirmed(true);
        setUpdatedMetadata(worker);
    }

    @RolesAllowed("authenticatedUser")
    public void changeMode(String login, boolean newMode, long version) throws BaseAppException {
        Account account = accountFacade.findByLogin(login);
        checkForOptimisticLock(account, version);

        account.setDarkMode(newMode);
        setUpdatedMetadata(account);
    }

    private boolean canUserRefreshToken(Account account, Set<String> tokenAccessLevels) {

        Set<String> accountValidAccessLevels = getAuthorizedAccessLevels(account)
                .map(a -> a.getAccessLevelType().name()).collect(Collectors.toSet());

        boolean accessLevelsValid = tokenAccessLevels.equals(accountValidAccessLevels) && !tokenAccessLevels.isEmpty();

        return account.isActive() && account.isConfirmed() && accessLevelsValid;
    }

    private Stream<AccessLevel> getAuthorizedAccessLevels(Account account) {
        return account.getAccessLevels().stream()
                .filter(accessLevel -> {
                    if (accessLevel instanceof BusinessWorker) {
                        BusinessWorker bw = (BusinessWorker) accessLevel;
                        return bw.isEnabled() && bw.isConfirmed();
                    }
                    return accessLevel.isEnabled();
                });
    }

    @PermitAll
    public Account updateAuthenticateInfo(String login, String ipAddr, LocalDateTime time, boolean isAuthValid) throws BaseAppException {

        Account account = accountFacade.findByLogin(login);
        String loggedString;
        try {
            if (isAuthValid) {
                account.setLastCorrectAuthenticationDateTime(time);
                account.setLastCorrectAuthenticationLogicalAddress(ipAddr);
                account.setNumberOfAuthenticationFailures(0);

                loggedString = String.format("Correct authentication for user %s" +
                                "\nAuthentication time: %s" +
                                "\nOrigin logical address: %s",
                        login, time, ipAddr);
                log.info(loggedString);
            } else {
                account.setLastIncorrectAuthenticationDateTime(time);
                account.setLastIncorrectAuthenticationLogicalAddress(ipAddr);
                account.setNumberOfAuthenticationFailures(account.getNumberOfAuthenticationFailures() + 1);

                loggedString = String.format("Authentication failure for user %s" +
                                "\nAuthentication time: %s" +
                                "\nOrigin logical address: %s" +
                                "\nNumber of consecutive authentication failures: %d",
                        login, time, ipAddr, account.getNumberOfAuthenticationFailures());
                log.warning(loggedString);
            }
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
        return account;
    }

    @PermitAll
    @Override
    public void sendAuthenticationCodeEmail(String login, boolean darkMode, LanguageType languageType) throws BaseAppException {
        Account account = accountFacade.findByLogin(login);
        account.setDarkMode(darkMode);
        account.setLanguageType(accountFacade.getLanguageTypeWrapperByLanguageType(languageType));
        accountFacade.edit(account);

        Locale locale = new Locale(account.getLanguageType().getName().name());
        String subject = i18n.getMessage(AUTH_CODE_EMAIL_SUBJECT, locale);
        String body = i18n.getMessage(AUTH_CODE_EMAIL_BODY, locale);
        String code = randomAlphanumeric(9);
        CodeWrapper codeWrapper = CodeWrapper.builder().code(code).account(account).used(false).build();
        this.codeWrapperFacade.create(codeWrapper);
        String contentHtml = "<p>" + body + "<br>" + code + "</p>";
        EmailService.sendEmailWithContent(account.getEmail().trim(), subject, contentHtml);
    }

    @PermitAll
    @Override
    public String authWCodeUpdateCorrectAuthenticateInfo(String login, String code, String IpAddr, LocalDateTime time) throws BaseAppException {
        Account account = this.accountFacade.findByLogin(login);
        CodeWrapper verificationCode;
        try {
            verificationCode = this.codeWrapperFacade.findByCode(code);
        } catch (BaseAppException e) {
            if (e.getMessage().equals(NO_SUCH_ELEMENT_ERROR)) {
                throw new AccountManagerException(CODE_IS_INCORRECT_ERROR);
            } else {
                throw new AccountManagerException(e.getMessage(), e);
            }
        }
        if (verificationCode.isUsed()) {
            updateIncorrectAuthenticateInfo(login, IpAddr, time);
            throw new AccountManagerException(CODE_ALREADY_USED_ERROR);
        }
        if (verificationCode.getCreationDateTime().plus(5, ChronoUnit.MINUTES).isBefore(time)) {
            updateIncorrectAuthenticateInfo(login, IpAddr, time);
            throw new AccountManagerException(CODE_EXPIRE_ERROR);
        }
        if (account.getId() != verificationCode.getAccount().getId()) {
            updateIncorrectAuthenticateInfo(login, IpAddr, time);
            throw new AccountManagerException(CODE_IS_INCORRECT_ERROR);
        }
        verificationCode.setUsed(true);
        this.codeWrapperFacade.edit(verificationCode);
        return updateCorrectAuthenticateInfo(login, IpAddr, time);
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public void changeLanguage(String login, long version) throws BaseAppException {
        Account account = accountFacade.findByLogin(login);
        checkForOptimisticLock(account, version);

        if (account.getLanguageType().getName() == LanguageType.EN) {
            account.setLanguageType(accountFacade.getLanguageTypeWrapperByLanguageType(LanguageType.PL));
        } else {
            account.setLanguageType(accountFacade.getLanguageTypeWrapperByLanguageType(LanguageType.EN));
        }
        setUpdatedMetadata(account);
    }
}

