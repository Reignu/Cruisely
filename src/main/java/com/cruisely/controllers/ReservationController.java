package com.cruisely.controllers;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.ControllerException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.cruise.dto.companies.CompanyLightDto;
import com.cruisely.cruise.dto.reservations.CruiseReservationDto;
import com.cruisely.cruise.dto.reservations.RemoveClientReservationDto;
import com.cruisely.cruise.dto.reservations.SelfReservationDto;
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
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;
import static com.cruisely.utils.TransactionRepeater.tryAndRepeat;

@Path("/reservation")
@RequestScoped
public class ReservationController {
    @Inject
    private ReservationEndpointLocal reservationEndpoint;

    /**
     * Retrieves information about reservations for a given cruise
     *
     * @return List of reservations
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/reservations-for-cruise/{cruiseUUID}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CruiseReservationDto> getCruisesForReservations(@PathParam("cruiseUUID") UUID cruiseUUID) throws BaseAppException {
        return tryAndRepeat(reservationEndpoint, () -> reservationEndpoint.viewCruiseReservations(cruiseUUID));
    }

    /**
     * Retrieves information about reservations for a given cruise for the logged business-worker
     *
     * @return List of reservations
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/reservations-for-worker-cruise/{cruiseUUID}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CruiseReservationDto> getWorkerCruisesForReservations(@PathParam("cruiseUUID") UUID cruiseUUID) throws BaseAppException {
        return tryAndRepeat(reservationEndpoint, () -> reservationEndpoint.viewWorkerCruiseReservations(cruiseUUID));
    }
    /**
     * Removes a client's reservation
     * @param clientLogin client's login
     * @param reservationUuid reservation UUID
     * @throws BaseAppException Base application exception
     */
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/{clientLogin}/{reservationUuid}")
    public void removeReservation(@PathParam("clientLogin") String clientLogin,
                                  @PathParam("reservationUuid") UUID reservationUuid
                                  ) throws BaseAppException {
        reservationEndpoint.removeClientReservation(new RemoveClientReservationDto(reservationUuid.toString(), clientLogin));
    }

    /**
     * Method responsible for returning the list of reservations of the currently logged-in client
     * @return List of reservations
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/self-reservations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SelfReservationDto> getSelfReservations() throws BaseAppException {
        return tryAndRepeat(reservationEndpoint, () -> reservationEndpoint.viewSelfCruiseReservations());
    }

    /**
     * Retrieves reservation metadata
     *
     * @param uuid UUID of the reservation selected for metadata
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getReservationMetadata(@PathParam("uuid") String uuid) throws BaseAppException {
        try{
            UUID convertedUUID = UUID.fromString(uuid);
            return tryAndRepeat(reservationEndpoint, () -> reservationEndpoint.getReservationMetadata(convertedUUID));
        }catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

}
