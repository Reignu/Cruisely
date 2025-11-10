package com.cruisely.cruise.managers;

import com.cruisely.entities.cruise.Attraction;
import com.cruisely.entities.cruise.Reservation;
import com.cruisely.exceptions.BaseAppException;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

/**
 * Class that manages business logic for reservations
 */
@Local
public interface ReservationManagerLocal {
    /**
     * Retrieves the list of reservations for a given cruise
     *
     * @param cruise_uuid UUID of the cruise for which to find reservations
     * @return List of reservations for the given cruise
     * @throws BaseAppException Base application exception
     */
    List<Reservation> getCruiseReservations(UUID cruise_uuid) throws BaseAppException;

    /**
     * Returns the list of reservations for a given cruise for a business worker
     *
     * @param cruise_uuid UUID of the cruise
     * @return List of reservations for the given cruise
     * @throws BaseAppException Base application exception
     */
    List<Reservation> getWorkerCruiseReservations(UUID cruise_uuid) throws BaseAppException;

    /**
     * Removes a client's reservation specified in the DTO
     *
     * @param reservationUuid UUID of the reservation
     * @param clientLogin Login of the client
     * @throws BaseAppException Base application exception
     */
    void removeClientReservation(UUID reservationUuid, String clientLogin) throws BaseAppException;

    /**
     * Reserves a cruise for a client
     * @param version Cruise version
     * @param cruiseUUID UUID of the cruise
     * @param numberOfSeats Number of seats to reserve
     * @param attractionsUUID UUIDs of selected attractions
     * @throws BaseAppException Base application exception
     */
    void createReservation(long version, UUID cruiseUUID, long numberOfSeats, List<String> attractionsUUID) throws BaseAppException;

    /**
     * Cancels a user's reservation
     * @param reservationUUID UUID of the reservation
     * @throws BaseAppException Base application exception
     */
    void cancelReservation(UUID reservationUUID) throws BaseAppException;

    /**
     * Returns the list of reservations of the currently logged-in client
     * @return list of reservations
     * @throws BaseAppException base application exception
     */
    List<Reservation> getClientReservations() throws BaseAppException;

    /**
     * Retrieves a reservation by its UUID
     *
     * @param uuid UUID of the reservation
     * @return reservation information
     * @throws BaseAppException exception thrown when the reservation is not found
     */
    Reservation findByUUID(UUID uuid) throws BaseAppException;
}
