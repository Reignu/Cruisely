package com.cruisely.cruise.managers;


import com.cruisely.entities.cruise.Attraction;
import com.cruisely.entities.cruise.Cruise;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.cruise.dto.cruises.CruiseGroupWithCruisesDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Class that manages business logic for cruises
 */

public interface CruiseManagerLocal {

    /**
     * Handles creation of a new cruise
     *
     * @param cruise object representing the cruise
     * @param cruiseGroupUUID UUID of the cruise group
     * @throws BaseAppException exception thrown when the creator account or cruise group is not found
     */
    void addCruise(Cruise cruise, UUID cruiseGroupUUID) throws BaseAppException;


    /**
     * Handles deactivation of an existing cruise
     *
     * @param uuid    UUID of the cruise
     * @param version version of the cruise object sent for optimistic locking
     * @throws BaseAppException exception thrown when the cruise, deactivating account, or version is invalid
     */
    void deactivateCruise(UUID uuid, Long version) throws BaseAppException;

    /**
     * Returns the cruise with the given UUID
     *
     * @param uuid UUID of the cruise
     * @return Cruise entity object
     * @throws BaseAppException exception thrown when the cruise is not found
     */
    Cruise getCruise(UUID uuid) throws BaseAppException;

    /**
     * Returns cruises belonging to the cruise group with the given UUID
     * @param uuid UUID of the cruise group
     * @return list of cruise entities belonging to the given cruise group
     * @throws BaseAppException exception thrown when cruises are not found
     */
    List<Cruise> getCruisesByCruiseGroup(UUID uuid) throws BaseAppException;

    /**
     * Publishes a cruise
     *
     * @param cruiseVersion version of the cruise entity
     * @param cruiseUuid    UUID of the cruise
     * @throws BaseAppException exception thrown when the cruise or publishing account is not found
     */
    void publishCruise(long cruiseVersion, UUID cruiseUuid) throws BaseAppException;

    /**
     * Handles editing of a cruise
     *
     * @param startDate   new start date for the cruise
     * @param endDate     new end date for the cruise
     * @param uuid        UUID of the cruise
     * @param version     version of the cruise object
     * @throws BaseAppException exception thrown when the cruise is not found or the version is invalid
     */
    void editCruise(LocalDateTime startDate, LocalDateTime endDate, UUID uuid, Long version) throws BaseAppException;

    /**
     * Returns all published cruises
     *
     * @return List of cruises
     */
    List<Cruise> getPublishedCruises() throws BaseAppException;

    /**
     * Finds a cruise by UUID
     *
     * @param uuid UUID of the cruise
     * @return Cruise information
     * @throws BaseAppException exception thrown when the cruise is not found
     */
    Cruise findByUUID(UUID uuid) throws BaseAppException;

}
