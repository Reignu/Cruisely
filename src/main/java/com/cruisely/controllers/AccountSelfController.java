package com.cruisely.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cruisely.common.IntegrityUtils;
import com.cruisely.common.dto.MetadataDto;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.ControllerException;
import com.cruisely.auth.dto.AccountMetadataDto;
import com.cruisely.auth.dto.AccountVerificationDto;
import com.cruisely.auth.dto.changedata.*;
import com.cruisely.auth.endpoints.AccountEndpointLocal;
import com.cruisely.cruise.dto.ratings.RatingDto;
import com.cruisely.cruise.endpoints.RatingEndpointLocal;
import com.cruisely.security.ETagFilterBinding;
import com.cruisely.security.EntityIdentitySignerVerifier;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.List;

import static com.cruisely.common.I18n.*;
import static com.cruisely.common.IntegrityUtils.checkEtagIntegrity;
import static com.cruisely.utils.TransactionRepeater.tryAndRepeat;

@Path("/self")
@RequestScoped
public class AccountSelfController {

    @Inject
    private AccountEndpointLocal accountEndpoint;

    @Inject
    private RatingEndpointLocal ratingEndpoint;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
    * Retrieves detailed information about the authenticated user's account
    *
    * @return Representation of account details as AccountDetailsViewDto serialized to JSON
     */
    @GET
    @Path("/account-details")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSelfDetails() throws BaseAppException, JsonProcessingException {
        return mapper.writeValueAsString(tryAndRepeat(accountEndpoint, () -> accountEndpoint.getSelfDetails()));
    }

    /**
     * Changes the current user's password according to data provided in the DTO; returns 204 on success
     *
     * @param accountChangeOwnPasswordDto DTO that holds the login, version, old and new password provided by the user
     * @param etag                        If-Match request header required to confirm data consistency
     */
    @ETagFilterBinding
    @PUT
    @Path("/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public void changeOwnPassword(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid AccountChangeOwnPasswordDto accountChangeOwnPasswordDto, @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(accountChangeOwnPasswordDto, etag);
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeOwnPassword(accountChangeOwnPasswordDto));
    }


    /**
     * Change client account data
     *
     * @param clientChangeDataDto DTO with the new data
     */
    @PUT
    @Path("/change-client-data")
    @Consumes(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public void changeClientData(@Valid ClientChangeDataDto clientChangeDataDto, @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(clientChangeDataDto, etag);

        tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeClientData(clientChangeDataDto));
    }

    /**
     * Change company employee account data
     *
     * @param businessWorkerChangeDataDto DTO with the new data
     */
    @PUT
    @Path("/change-business-worker-data")
    @Consumes(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public void changeBusinessWorkerData(@Valid BusinessWorkerChangeDataDto businessWorkerChangeDataDto, @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(businessWorkerChangeDataDto, etag);
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeBusinessWorkerData(businessWorkerChangeDataDto));
    }

    /**
     * Change moderator account data
     *
     * @param moderatorChangeDataDto DTO with the new data
     */
    @PUT
    @Path("/change-moderator-data")
    @Consumes(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public void changeModeratorData(@Valid ModeratorChangeDataDto moderatorChangeDataDto, @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(moderatorChangeDataDto, etag);
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeModeratorData(moderatorChangeDataDto));
    }

    /**
     * Change administrator account data
     *
     * @param administratorChangeDataDto DTO with the new data
     */
    @PUT
    @Path("/change-administrator-data")
    @Consumes(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public void changeAdministratorData(@Valid AdministratorChangeDataDto administratorChangeDataDto, @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(administratorChangeDataDto, etag);
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeAdministratorData(administratorChangeDataDto));
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
     * Changes the current theme mode
     *
     * @param changeModeDto DTO representing the submitted data
     * @param etag          If-Match request header
     * @throws BaseAppException Base application exception
     */
    @PUT
    @Path("/change-theme-mode")
    @Consumes(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public void changeMode(@Valid ChangeModeDto changeModeDto, @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(changeModeDto, etag);
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeMode(changeModeDto));
    }

    /**
     * Method changes the user's language
     * @param changeLanguageDto DTO representing the submitted data
     * @param etag If-Match request header
     * @throws BaseAppException Base application exception
     */
    @PUT
    @Path("/change-language")
    @Consumes(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public void changeLanguage(@Valid ChangeLanguageDto changeLanguageDto, @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(changeLanguageDto, etag);
        tryAndRepeat(accountEndpoint, () -> accountEndpoint.changeLanguage(changeLanguageDto));
    }

    /**
     * Retrieves metadata of the current user
     *
     * @return DTO form of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountMetadataDto getSelfAccountMetadata() throws BaseAppException {
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.getSelfMetadata());
    }

    /**
     * Retrieves metadata for the selected access level of the currently logged-in user
     *
     * @param accessLevel selected access level
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/access-level/{access-level}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getSelfAccessLevelMetadata(@PathParam("access-level") String accessLevel) throws BaseAppException {
        AccessLevelType accessLevelType;
        try {
            accessLevelType = AccessLevelType.valueOf(accessLevel.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new ControllerException(ACCESS_LEVEL_PARSE_ERROR);
        }
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.getSelfAccessLevelMetadata(accessLevelType));
    }

    /**
     * Retrieves metadata for the address of the currently logged-in client
     *
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/address/")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getSelfAddressMetadata() throws BaseAppException {
        return tryAndRepeat(accountEndpoint, () -> accountEndpoint.getSelfAddressMetadata());
    }

    /**
     * Retrieves all ratings for the logged-in user
     *
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/ratings")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RatingDto> getOwnRatings() throws BaseAppException {
        return ratingEndpoint.getOwnRatings();
    }
}
