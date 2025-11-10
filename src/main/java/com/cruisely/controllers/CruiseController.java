package com.cruisely.controllers;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.ControllerException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.cruise.dto.cruises.*;
import com.cruisely.cruise.dto.reservations.CreateReservationDto;
import com.cruisely.cruise.endpoints.CruiseEndpointLocal;
import com.cruisely.cruise.endpoints.ReservationEndpointLocal;
import com.cruisely.security.ETagFilterBinding;
import com.cruisely.security.EntityIdentitySignerVerifier;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;
import static com.cruisely.common.IntegrityUtils.checkEtagIntegrity;
import static com.cruisely.utils.TransactionRepeater.tryAndRepeat;

/**
 * Class exposing the API for performing operations on cruises
 */
@Path("/cruise")
@RequestScoped
public class CruiseController {

    @Inject
    ReservationEndpointLocal reservationEndpoint;
    @Inject
    private CruiseEndpointLocal cruiseEndpoint;

    /**
     * Retrieves information about the cruise with the given UUID
     *
     * @return object representing the cruise
     * @throws BaseAppException exception thrown when the entity is not found
     */
    @GET
    @Path("/get-cruise/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public CruiseDto getCruiseByUUID(@PathParam("uuid") String strUUID) throws BaseAppException {
        UUID uuid;
        try {
            uuid = UUID.fromString(strUUID);
        } catch (IllegalArgumentException e) {
            throw new ControllerException(NO_SUCH_ELEMENT_ERROR);
        }
        return tryAndRepeat(cruiseEndpoint, () -> cruiseEndpoint.getCruise(UUID.fromString(strUUID)));
    }

    /**
     * Retrieves cruises belonging to the cruise group with the given UUID
     *
     * @param uuid UUID of the cruise group
     * @return list of cruises belonging to the cruise group with the given UUID
     * @throws BaseAppException exception thrown when the entity is not found
     */
    @GET
    @Path("/cruise_group/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RelatedCruiseDto> getCruisesByCruiseGroupUUID(@PathParam("uuid") String uuid) throws BaseAppException {
        try {
            UUID convertedUUID = UUID.fromString(uuid);
            return tryAndRepeat(cruiseEndpoint, () -> cruiseEndpoint.getCruisesByCruiseGroup(convertedUUID));
        } catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

    /**
     * Retrieves information about published cruises
     *
     * @return List of published cruises
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/cruises")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CruiseGroupWithCruisesDto> getAllCruises() throws BaseAppException {
        return tryAndRepeat(cruiseEndpoint, () -> cruiseEndpoint.getPublishedCruises());
    }

    /**
     * Retrieves cruises for a given cruise group
     *
     * @param cruiseGroupUUID UUID of the cruise group
     * @return List of cruises in DTO representation
     * @throws BaseAppException Base application exception thrown in case of business rule violations
     */
    @GET
    @Path("/cruises-for-group/{cruise-group-uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CruiseForCruiseGroupDto> getCruisesForCruiseGroup(@PathParam("cruise-group-uuid") String cruiseGroupUUID) throws BaseAppException {
        try {
            UUID uuid = UUID.fromString(cruiseGroupUUID);
            return tryAndRepeat(cruiseEndpoint, () -> cruiseEndpoint.getCruisesForCruiseGroup(uuid));
        } catch (BaseAppException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

    /**
     * Method creating a reservation
     *
     * @param reservationDto Information about the reservation being created
     * @throws BaseAppException Base application exception
     */
    @POST
    @Path("/reserve")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createReservation(@Valid CreateReservationDto reservationDto) throws BaseAppException {
        tryAndRepeat(reservationEndpoint, () -> reservationEndpoint.createReservation(reservationDto));
    }


    /**
     * Method canceling a client's reservation
     *
     * @param reservationUUID UUID of the canceled reservation
     * @throws BaseAppException Base application exception
     */
    @DELETE
    @Path("/cancelReservation/{reservationUUID}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void cancelReservation(@PathParam("reservationUUID") UUID reservationUUID) throws BaseAppException {
        tryAndRepeat(reservationEndpoint, () -> reservationEndpoint.cancelReservation(reservationUUID));
    }

    /**
     * Method creating a new cruise
     *
     * @param newCruiseDto Object representing the new cruise
     * @throws BaseAppException Base application exception
     */
    @POST
    @Path("/new-cruise")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createCruise(@Valid @NotNull(message = CONSTRAINT_NOT_NULL) NewCruiseDto newCruiseDto) throws BaseAppException {
        tryAndRepeat(cruiseEndpoint, () -> cruiseEndpoint.addCruise(newCruiseDto));
    }


    /**
     * Method deactivating a cruise
     *
     * @param deactivateCruiseDto Object containing UUID and version of the cruise
     * @param etag                If-Match header required to confirm data consistency
     * @throws BaseAppException Base application exception
     */
    @ETagFilterBinding
    @PUT
    @Path("/deactivate-cruise")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deactivateCruise(@Valid @NotNull(message = CONSTRAINT_NOT_NULL) DeactivateCruiseDto deactivateCruiseDto, @HeaderParam("If-Match") @NotNull(message = CONSTRAINT_NOT_NULL) @NotEmpty(message = CONSTRAINT_NOT_EMPTY) @Valid String etag) throws BaseAppException {
        checkEtagIntegrity(deactivateCruiseDto, etag);
        tryAndRepeat(cruiseEndpoint, () -> cruiseEndpoint.deactivateCruise(deactivateCruiseDto));
    }


    /**
     * @param editCruiseDto Object representing the cruise
     * @param etag          If-Match header required to confirm data consistency
     * @throws BaseAppException Base application exception
     */
    @ETagFilterBinding
    @PUT
    @Path("/edit-cruise")
    @Consumes(MediaType.APPLICATION_JSON)
    public void editCruise(@Valid @NotNull(message = CONSTRAINT_NOT_NULL) EditCruiseDto editCruiseDto, @HeaderParam("If-Match") @NotNull(message = CONSTRAINT_NOT_NULL) @NotEmpty(message = CONSTRAINT_NOT_EMPTY) @Valid String etag) throws BaseAppException {
        checkEtagIntegrity(editCruiseDto, etag);
        tryAndRepeat(cruiseEndpoint, () -> cruiseEndpoint.editCruise(editCruiseDto));
    }

    /**
     * Changes the cruise status to published
     *
     * @param publishCruiseDto object representing the cruise
     * @param etag          If-Match header required to confirm data consistency
     * @throws BaseAppException Base application exception
     */
    @ETagFilterBinding
    @PUT
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    public void publishCruise(@Valid PublishCruiseDto publishCruiseDto,
                              @HeaderParam("If-Match") @NotNull(message = CONSTRAINT_NOT_NULL)
                              @NotEmpty(message = CONSTRAINT_NOT_EMPTY) @Valid String etag) throws BaseAppException {
        checkEtagIntegrity(publishCruiseDto, etag);
        tryAndRepeat(cruiseEndpoint, () -> cruiseEndpoint.publishCruise(publishCruiseDto));
    }

    /**
     * Retrieves cruise metadata
     *
     * @param uuid UUID of the cruise selected for metadata
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getCruiseMetadata(@PathParam("uuid") String uuid) throws BaseAppException {
        try{
            UUID convertedUUID = UUID.fromString(uuid);
            return tryAndRepeat(cruiseEndpoint, () -> cruiseEndpoint.getCruiseMetadata(convertedUUID));
        }catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

}
