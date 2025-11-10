package com.cruisely.auth.endpoints;


import com.cruisely.common.endpoints.BaseEndpoint;
import com.cruisely.entities.auth.LanguageType;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.auth.dto.AuthenticateDto;
import com.cruisely.auth.managers.AccountManagerLocal;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.time.LocalDateTime;

/**
 * Class responsible for user authentication.
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@PermitAll
@Interceptors(TrackingInterceptor.class)
public class AuthenticateEndpoint extends BaseEndpoint implements AuthenticateEndpointLocal {

    @Inject
    private AccountManagerLocal accountManager;

    @PermitAll
    @Override
    public void updateIncorrectAuthenticateInfo(String login, String IpAddr, LocalDateTime time) throws BaseAppException {
        accountManager.updateIncorrectAuthenticateInfo(login, IpAddr, time);
    }

    @PermitAll
    @Override
    public String updateCorrectAuthenticateInfo(String login, String IpAddr, LocalDateTime time) throws BaseAppException {
        return accountManager.updateCorrectAuthenticateInfo(login, IpAddr, time);
    }

    @PermitAll
    @Override
    public void sendAuthenticationCodeEmail(AuthenticateDto auth) throws BaseAppException {
        accountManager.sendAuthenticationCodeEmail(auth.getLogin(), auth.getDarkMode(), LanguageType.valueOf(auth.getLanguage()));
    }

    @PermitAll
    @Override
    public String authWCodeUpdateCorrectAuthenticateInfo(String login, String code, String IpAddr, LocalDateTime time) throws BaseAppException {
        return accountManager.authWCodeUpdateCorrectAuthenticateInfo(login, code, IpAddr, time);
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public String refreshToken(String token) throws BaseAppException {
        return accountManager.refreshJWTToken(token);
    }
}
