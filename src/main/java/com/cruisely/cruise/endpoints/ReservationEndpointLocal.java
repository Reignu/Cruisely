package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.TransactionalEndpoint;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.cruise.dto.reservations.*;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

/**
 * Interface that handles collecting mapped DTO objects to model objects related to reservations, and calls logic methods passing mapped objects.
 */
@Local
public interface ReservationEndpointLocal extends TransactionalEndpoint {
    /**
     * Retrieves all reservations for a given cruise
     *
     * @param cruise_uuid cruise identifier
     * @return list of reservations
     * @throws BaseAppException Base application exception
     */
    List<CruiseReservationDto> viewCruiseReservations(UUID cruise_uuid) throws BaseAppException;

    /**
     * Retrieves all reservations for a given cruise that belongs to a business worker
     *
     * @param cruise_uuid cruise identifier
     * @return list of reservations
     * @throws BaseAppException Base application exception
     */
    List<CruiseReservationDto> viewWorkerCruiseReservations(UUID cruise_uuid) throws BaseAppException;

    /**
     * Removes client cruise specified in the dto object
     *
     * @param removeClientReservationDto
     * @return
     * @throws BaseAppException
     */
    void removeClientReservation(RemoveClientReservationDto removeClientReservationDto) throws BaseAppException;

    /**
     * Reserves a cruise for the specified client
     * @param createReservationDto Information about client and cruise
     * @throws BaseAppException Base application exception
     */
    void createReservation(CreateReservationDto createReservationDto) throws BaseAppException;

    /**
     * Method for canceling a reserved cruise
     * @param reservationUUID UUID of the canceled reservation
     * @throws BaseAppException Base application exception
     */
    void cancelReservation(UUID reservationUUID) throws BaseAppException;


    /**
     * Method returning a list of reservations of the currently logged in client
     * @return list of client reservations
     * @throws BaseAppException base application exception
     */
    List<SelfReservationDto> viewSelfCruiseReservations() throws BaseAppException;

    /**
     * Retrieves reservation metadata
     *
     * @param uuid UUID of reservation selected for metadata
     * @return DTO representation of metadata
     * @throws BaseAppException Base application exception
     */
    MetadataDto getReservationMetadata(UUID uuid) throws BaseAppException;
}

