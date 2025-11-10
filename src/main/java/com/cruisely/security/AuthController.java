package com.cruisely.security;

import com.cruisely.common.I18n;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.ControllerException;
import com.cruisely.auth.dto.AuthenticateCodeDto;
import com.cruisely.auth.dto.AuthenticateDto;
import com.cruisely.auth.dto.registration.BusinessWorkerForRegistrationDto;
import com.cruisely.auth.dto.registration.ClientForRegistrationDto;
import com.cruisely.auth.endpoints.AccountEndpointLocal;
import com.cruisely.auth.endpoints.AuthenticateEndpointLocal;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

import static com.cruisely.common.I18n.CONSTRAINT_NOT_NULL;
import static com.cruisely.utils.TransactionRepeater.tryAndRepeat;

/**
 * Class that exposes authentication API endpoints
 */
@Path("/auth")
@RequestScoped
public class AuthController {

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Context
    private HttpServletRequest httpServletRequest;

    @Inject
    private AuthenticateEndpointLocal authEndpoint;

    @Inject
    private AccountEndpointLocal accountEndpoint;

    /**
     * Method used for sign-in. Sends a code via email for two-factor authentication.
     *
     * @param auth user's login and password
     * @return HTTP response; on successful credentials returns token (via second step)
     */
    @Path("/sign-in")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(@Valid @NotNull(message = CONSTRAINT_NOT_NULL) AuthenticateDto auth) throws BaseAppException {
        Credential credential = auth.toCredential();
        CredentialValidationResult result = identityStoreHandler.validate(credential);

        if (result.getStatus() != CredentialValidationResult.Status.VALID) {
            try {
                authEndpoint.updateIncorrectAuthenticateInfo(auth.getLogin(), httpServletRequest.getRemoteAddr(), LocalDateTime.now());
            } catch (BaseAppException e) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).entity(I18n.INCORRECT_PASSWORD).build();
        }
        authEndpoint.sendAuthenticationCodeEmail(auth);
        return Response.ok().build();
    }

    /**
     * Refreshes the user's JWT token
     *
     * @param tokenString Authorization header value containing the token
     * @return Refreshed token
     * @throws BaseAppException Base application exception
     */
    @POST
    @Path("/refresh-token/")
    @Produces(MediaType.TEXT_PLAIN)
    public String refreshJWTToken(@HeaderParam("Authorization") String tokenString) throws BaseAppException {
        if (!tokenString.contains("Bearer ")) {
            throw new ControllerException("Invalid authorization header");
        }
        String token = tokenString.substring("Bearer ".length());
        return authEndpoint.refreshToken(token);
    }


    /**
     * Creates a new client account
     *
     * @param clientForRegistrationDto Data required to create a client account
     */
    @POST
    @Path("/client/registration")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createClient(@Valid @NotNull(message = CONSTRAINT_NOT_NULL) ClientForRegistrationDto clientForRegistrationDto) throws BaseAppException {
        accountEndpoint.createClientAccount(clientForRegistrationDto);
    }

    /**
     * Creates a new business-worker account
     *
     * @param businessWorkerForRegistrationDto Data required to create a business-worker account
     */
    @POST
    @Path("/business-worker/registration")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createBusinessWorker(@Valid @NotNull(message = CONSTRAINT_NOT_NULL) BusinessWorkerForRegistrationDto businessWorkerForRegistrationDto) throws BaseAppException {
        accountEndpoint.createBusinessWorkerAccount(businessWorkerForRegistrationDto);
    }

    /**
     * Method for signing in using the code sent during the first authentication phase
     *
     * @param auth login and the code sent via email
     * @return JWT token
     * @throws BaseAppException Base application exception
     */
    @Path("/code-sign-in")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String doubleAuth(@Valid @NotNull(message = CONSTRAINT_NOT_NULL) AuthenticateCodeDto auth) throws BaseAppException {
        return authEndpoint.authWCodeUpdateCorrectAuthenticateInfo(auth.getLogin(), auth.getCode(), httpServletRequest.getRemoteAddr(), LocalDateTime.now());
    }
}