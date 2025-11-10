package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.TransactionalEndpoint;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.cruise.dto.cruises.*;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;


/**
 * Interface that handles DTO objects related to cruises
 */
@Local
public interface CruiseEndpointLocal extends TransactionalEndpoint {

    /**
     * Creating a new cruise
     *
     * @param newCruiseDto object representing the cruise
     * @throws BaseAppException exception thrown when the account of the creator or the cruise group is not found
     */
    void addCruise(NewCruiseDto newCruiseDto) throws BaseAppException;


    /**
     * Deactivating a cruise
     *
     * @param deactivateCruiseDto DTO object containing the UUID and version of the cruise
     * @throws BaseAppException exception thrown when the cruise, the deactivating account, or the version is invalid
     */
    void deactivateCruise(DeactivateCruiseDto deactivateCruiseDto) throws BaseAppException;

    /**
     * Obtains information about a cruise with the given UUID
     *
     * @param uuid cruise UUID
     * @return DTO object containing cruise information
     * @throws BaseAppException exception thrown when the cruise or related account is not found
     */
    CruiseDto getCruise(UUID uuid) throws BaseAppException;

    /**
     * Returns cruises that belong to the cruise group with the given UUID
     * @param uuid UUID of the cruise group
     * @return list of cruise entities belonging to the given cruise group
     * @throws BaseAppException exception thrown when cruises are not found
     */
    List<RelatedCruiseDto> getCruisesByCruiseGroup(UUID uuid) throws BaseAppException;

    /**
     * Publishes a cruise
     *
     * @param publishCruiseDto DTO required to publish the cruise
     * @throws BaseAppException exception thrown when the cruise or publishing account is not found
     */
    void publishCruise(PublishCruiseDto publishCruiseDto) throws BaseAppException;

    /**
     * Edit cruise
     *
     * @param editCruiseDto DTO containing changes, version and UUID
     * @throws BaseAppException exception thrown when the cruise, account, or version is invalid
     */
    void editCruise(EditCruiseDto editCruiseDto) throws BaseAppException;

    /**
     * Returns all published cruises
     *
     * @return List of cruise groups
     * @throws BaseAppException Base application exception
     */
    List<CruiseGroupWithCruisesDto> getPublishedCruises() throws BaseAppException;

    /**
     * Retrieves information about cruises for a given cruise group
     *
     * @param cruiseGroupUUID UUID of the cruise group
     *     * @return List of cruises in DTO representation
     */
    List<CruiseForCruiseGroupDto> getCruisesForCruiseGroup(UUID cruiseGroupUUID) throws BaseAppException;

    /**
     * Retrieves cruise metadata
     *
     * @param uuid UUID of the cruise selected for metadata
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    MetadataDto getCruiseMetadata(UUID uuid) throws BaseAppException;
}
