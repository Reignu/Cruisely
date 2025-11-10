package com.cruisely.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cruisely.common.IntegrityUtils;
import com.cruisely.common.dto.MetadataDto;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.ControllerException;
import com.cruisely.auth.dto.*;
import com.cruisely.auth.dto.changedata.AccountChangeEmailDto;
import com.cruisely.auth.dto.changedata.OtherAccountChangeDataDto;
import com.cruisely.auth.dto.changedata.OtherBusinessWorkerChangeDataDto;
import com.cruisely.auth.dto.changedata.OtherClientChangeDataDto;
import com.cruisely.auth.dto.changes.ChangeAccessLevelStateDto;
import com.cruisely.auth.dto.changes.GrantAccessLevelDto;
import com.cruisely.auth.endpoints.AccountEndpointLocal;
import com.cruisely.security.ETagFilterBinding;
import com.cruisely.security.EntityIdentitySignerVerifier;
import com.cruisely.validators.Login;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.cruisely.common.I18n.*;
import static com.cruisely.common.IntegrityUtils.checkEtagIntegrity;
import static com.cruisely.utils.TransactionRepeater.tryAndRepeat;

/**
 * Class that exposes REST API for performing operations on user accounts and handles data validation.
 */
@Path("/account")
@RequestScoped
public class AccountController {
    @Inject
    private AccountEndpointLocal accountEndpoint;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Retrieves a user by login and creates an ETag based on it
     *
     * @param login user's login
     * @return Server response with a JSON representation of the user object
     * and an ETag header generated from the object
     */
    @GET
    @Path("/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountByLogin(@PathParam("login") @Login @Valid String login) throws BaseAppException {
        AccountDto account = tryAndRepeat(accountEndpoint, () -> accountEndpoint.getAccountByLogin(login));
        String ETag = EntityIdentitySignerVerifier.calculateEntitySignature(account);
        return Response.ok().entity(account).header("ETag", ETag).build();
    }

    /**
     * Retrieves user details including access levels
     *
     * @param login user's login
     * @return Server response with a JSON representation of the user's details
     */
    @GET
    @Path("/details/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAccountDetailsByLogin(@PathParam("login") @Valid @Login String login) throws BaseAppException, JsonProcessingException {
        return mapper.writeValueAsString(tryAndRepeat(accountEndpoint, () -> accountEndpoint.getAccountDetailsByLogin(login)));
    }

    /**
     * Retrieves a client by login
     * @param login client's login
     * @return Response with the client
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/client/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientByLogin(@PathParam("login") @Valid @Login String login) throws BaseAppException {
        ClientDto client = tryAndRepeat(accountEndpoint, () -> accountEndpoint.getClientByLogin(login));
        String ETag = EntityIdentitySignerVerifier.calculateEntitySignature(client);
        return Response.ok().entity(client).header("ETag", ETag).build();
    }

    /**
     * Retrieves a company employee by login
     * @param login employee's login
     * @return Response with the employee
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/business-worker/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBusinessWorkerByLogin(@PathParam("login") @Valid @Login String login) throws BaseAppException {
        BusinessWorkerDto businessWorker = tryAndRepeat(accountEndpoint, () -> accountEndpoint.getBusinessWorkerByLogin(login));
        String ETag = EntityIdentitySignerVerifier.calculateEntitySignature(businessWorker);
        return Response.ok().entity(businessWorker).header("ETag", ETag).build();
    }

    /**
     * Retrieves a moderator by login
     * @param login moderator's login
     * @return Response with the moderator
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/moderator/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModeratorByLogin(@PathParam("login") @Valid @Login String login) throws BaseAppException {
        ModeratorDto moderator = tryAndRepeat(accountEndpoint, () -> accountEndpoint.getModeratorByLogin(login));
        String ETag = EntityIdentitySignerVerifier.calculateEntitySignature(moderator);
        return Response.ok().entity(moderator).header("ETag", ETag).build();
    }

    /**
     * Retrieves an administrator by login
     * @param login administrator's login
     * @return Response with the administrator
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/administrator/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdministratorByLogin(@PathParam("login") @Valid @Login String login) throws BaseAppException {
        AdministratorDto administrator = tryAndRepeat(accountEndpoint, () -> accountEndpoint.getAdministratorByLogin(login));
        String ETag = EntityIdentitySignerVerifier.calculateEntitySignature(administrator);
        return Response.ok().entity(administrator).header("ETag", ETag).build();
    }

    /**
     * Retrieves information about all accounts
     *
     * @return List of accounts
     */
    @GET
    @Path("/accounts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AccountDtoForList> getAllAccounts() throws BaseAppException {
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.getAllAccounts());
    }

    /**
     * Retrieves the list of all unconfirmed company employees
     *
     * @return list of DTOs with company employees
     * @throws BaseAppException base application exception
     */
    @GET
    @Path("/unconfirmed-business-workers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BusinessWorkerWithCompanyDto> getAllUnconfirmedBusinessWorkers() throws BaseAppException {
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.getAllUnconfirmedBusinessWorkers());
    }

    /**
     * Method indirectly responsible for blocking a user
     *
     * @param blockAccountDto Object with the user's login to block and version
     * @param etag            ETag value
     */
    @ETagFilterBinding
    @PUT
    @Path("/block")
    @Consumes(MediaType.APPLICATION_JSON)
    public void blockUser(@Valid BlockAccountDto blockAccountDto, @HeaderParam("If-Match") @NotNull(message = CONSTRAINT_NOT_NULL) @NotEmpty(message = CONSTRAINT_NOT_EMPTY) @Valid String etag) throws BaseAppException {
        checkEtagIntegrity(blockAccountDto, etag);
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.blockUser(blockAccountDto.getLogin(), blockAccountDto.getVersion()));
    }

    /**
     * @param unblockAccountDto Object containing the login of the user to unblock
     * @param etag              ETag value
     */
    @ETagFilterBinding
    @PUT
    @Path("/unblock")
    @Consumes(MediaType.APPLICATION_JSON)
    public void unblockUser(@Valid UnblockAccountDto unblockAccountDto, @HeaderParam("If-Match") @NotNull(message = CONSTRAINT_NOT_NULL) @NotEmpty(message = CONSTRAINT_NOT_EMPTY) String etag) throws BaseAppException {
        checkEtagIntegrity(unblockAccountDto, etag);
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.unblockUser(unblockAccountDto.getLogin(), unblockAccountDto.getVersion()));
    }

    /**
     * Adds an access level to an existing account
     *
     * @param grantAccessLevel DTO carrying data necessary to grant the access level
     * @return Server response representing the AccountDto after changes in JSON format
     */
    @ETagFilterBinding
    @PUT
    @Path("/grant-access-level")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AccountDto grantAccessLevel(@Valid GrantAccessLevelDto grantAccessLevel, @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(grantAccessLevel, etag);
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.grantAccessLevel(grantAccessLevel));
    }

    /**
     * Change the state of a given access level (enable/disable)
     *
     * @param changeAccessLevelStateDto DTO carrying data needed to change the access level state
     * @param etag                      If-Match header required to confirm data consistency
     * @return Server response representing the AccountDto after changes in JSON format
     */
    @ETagFilterBinding
    @PUT
    @Path("/change-access-level-state")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AccountDto changeAccessLevelState(@Valid ChangeAccessLevelStateDto changeAccessLevelStateDto, @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(changeAccessLevelStateDto, etag);
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeAccessLevelState(changeAccessLevelStateDto));
    }


    /**
     * Method responsible for resetting the password
     *
     * @param passwordResetDto DTO object containing necessary data for password reset
     */
    @PUT
    @Path("/reset-password")
    public void resetPassword(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid PasswordResetDto passwordResetDto) throws BaseAppException {
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.resetPassword(passwordResetDto));
    }

    /**
     * Method responsible for handling password reset requests
     *
     * @param login user's login
     */
    @POST
    @Path("/request-password-reset/{login}")
    public void requestPasswordReset(@PathParam("login") @Valid @Login String login) throws BaseAppException {
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.requestPasswordReset(login));
    }

    /**
     * Method responsible for handling password reset requests for a given user
     *
     * @param login user's login
     * @param email user's email
     */
    @POST
    @Path("/request-someones-password-reset/{login}/{email}")
    public void requestSomeonesPasswordReset(@PathParam("login") @Valid @Login String login, @PathParam("email")
    @Valid @Email(message = REGEX_INVALID_EMAIL) @NotEmpty(message = CONSTRAINT_NOT_EMPTY) String email) throws BaseAppException {
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.requestSomeonesPasswordReset(login, email));
    }


    /**
     * Method responsible for verifying an account
     *
     * @param accountVerificationDto DTO object containing necessary data for verification
     */
    @PUT
    @Path("/verify")
    public void verifyAccount(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid AccountVerificationDto accountVerificationDto) throws BaseAppException {
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.verifyAccount(accountVerificationDto));
    }

    /**
     * Change data of a selected account with client access level
     *
     * @param otherClientChangeDataDto DTO with new data
     * @param etag                     If-Match header required to confirm data consistency
     * @return Server response in JSON format
     */
    @PUT
    @Path("/change-client-data")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public OtherClientChangeDataDto changeOtherClientData(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid OtherClientChangeDataDto otherClientChangeDataDto,
                                                          @HeaderParam("If-Match") String etag) throws BaseAppException {

        checkEtagIntegrity(otherClientChangeDataDto, etag);
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeOtherClientData(otherClientChangeDataDto));
    }

    /**
     * Change data of a selected account with businessWorker access level
     *
     * @param otherBusinessWorkerChangeDataDto DTO with new data
     * @param etag                             If-Match header required to confirm data consistency
     * @return Server response in JSON format
     */
    @PUT
    @Path("/change-business-worker-data")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public OtherBusinessWorkerChangeDataDto changeOtherBusinessWorkerData(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid OtherBusinessWorkerChangeDataDto otherBusinessWorkerChangeDataDto,
                                                                          @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(otherBusinessWorkerChangeDataDto, etag);

        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeOtherBusinessWorkerData(otherBusinessWorkerChangeDataDto));
    }

    /**
     * Change data of a selected account with moderator or administrator access level
     *
     * @param otherAccountChangeDataDto DTO with new data
     * @param etag                      If-Match header required to confirm data consistency
     * @return Server response in JSON format
     */
    @PUT
    @Path("/change-account-data")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public AccountDto changeOtherAccountData(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid OtherAccountChangeDataDto otherAccountChangeDataDto,
                                             @HeaderParam("If-Match") String etag) throws BaseAppException {

        checkEtagIntegrity(otherAccountChangeDataDto, etag);
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeOtherAccountData(otherAccountChangeDataDto));
    }

    /**
     * Change email according to data provided in the DTO
     *
     * @param accountVerificationDto DTO with the token
     */
    @PUT
    @Path("/change-email")
    @Consumes(MediaType.APPLICATION_JSON)
    public void changeEmail(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid AccountVerificationDto accountVerificationDto) throws BaseAppException {
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeEmail(accountVerificationDto));
    }

    /**
     * Confirms the specified company employee
     *
     * @param blockAccountDto DTO containing the version and login of the employee
     * @param etag            If-Match header required to confirm data consistency
     * @throws BaseAppException base application exception
     */
    @PUT
    @Path("/confirm-business-worker")
    @ETagFilterBinding
    @Consumes(MediaType.APPLICATION_JSON)
    public void confirmBusinessWorker(@Valid BlockAccountDto blockAccountDto, @HeaderParam("If-Match") @NotNull(message = CONSTRAINT_NOT_NULL) @NotEmpty(message = CONSTRAINT_NOT_EMPTY) String etag) throws BaseAppException {
        checkEtagIntegrity(blockAccountDto, etag);
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.confirmBusinessWorker(blockAccountDto));
    }

    /**
     * Sends a request to change own email
     * @param accountChangeEmailDto DTO with login, version and new email
     * @throws BaseAppException Base application exception
     */
    @POST
    @Path("/request-email-change")
    public void requestEmailChange(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid AccountChangeEmailDto accountChangeEmailDto) throws BaseAppException {
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.requestEmailChange(accountChangeEmailDto));
    }

    /**
     * Sends a request to change someone else's email
     * @param accountChangeEmailDto DTO with login, version and new email
     * @throws BaseAppException Base application exception
     */
    @POST
    @Path("/request-other-email-change")
    public void requestOtherEmailChange(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid AccountChangeEmailDto accountChangeEmailDto) throws BaseAppException {
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.requestOtherEmailChange(accountChangeEmailDto));
    }

    /**
     * Retrieves user metadata
     *
     * @param login user's login
     * @return DTO form of metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountMetadataDto getAccountMetadata(@PathParam("login") String login) throws BaseAppException {
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.getAccountMetadata(login));
    }


    /**
     * Retrieves metadata for the selected access level of a user
     *
     * @param login       user's login
     * @param accessLevel selected access level
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/access-level/{access-level}/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getAccessLevelMetadata(@PathParam("login") String login,
                                              @PathParam("access-level") String accessLevel) throws BaseAppException {
        AccessLevelType accessLevelType;
        try {
            accessLevelType = AccessLevelType.valueOf(accessLevel.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new ControllerException(ACCESS_LEVEL_PARSE_ERROR);
        }
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.getAccessLevelMetadata(login, accessLevelType));
    }


    /**
     * Retrieves metadata for the address of a given user
     *
     * @param login user's login
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/address/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getClientAddressMetadata(@PathParam("login") String login) throws BaseAppException {
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.getAddressMetadata(login));
    }
}