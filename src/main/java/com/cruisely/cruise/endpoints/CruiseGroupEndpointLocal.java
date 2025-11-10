package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.TransactionalEndpoint;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.cruise.dto.cruiseGroups.CruiseGroupWithDetailsDto;
import com.cruisely.cruise.dto.cruiseGroups.AddCruiseGroupDto;
import com.cruisely.cruise.dto.cruiseGroups.ChangeCruiseGroupDto;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

/**
 * Interface responsible for collecting mapped DTO objects into model objects related to cruise groups
 * and invoking business logic methods with the mapped objects.
 */
@Local
public interface CruiseGroupEndpointLocal extends TransactionalEndpoint {
    /**
     * Creates a new cruise group based on the provided DTO
     *
     * @param addCruiseGroupDto DTO object containing data required to create the cruise group
     * @throws BaseAppException Base application exception
     */
    void addCruiseGroup(AddCruiseGroupDto addCruiseGroupDto) throws BaseAppException;

    /**
     * Allows editing of a cruise group
     *
     * @param changeCruiseGroup DTO object containing data required to edit the cruise group
     * @throws BaseAppException Base application exception
     */
    void changeCruiseGroup(ChangeCruiseGroupDto changeCruiseGroup) throws BaseAppException;


    /**
     * Returns a list of DTO objects presenting information about cruise groups
     *
     * @return list of DTO objects representing cruise group information
     */
    List<CruiseGroupWithDetailsDto> getCruiseGroupsInfo() throws BaseAppException;


    /**
     * Method responsible for invoking the logic to deactivate a cruise group
     *
     * @param uuid UUID of the cruise group to deactivate
     * @param version version of the cruise group to deactivate
     * @throws BaseAppException Base application exception
     */
    void deactivateCruiseGroup(UUID uuid, Long version) throws BaseAppException;

    /**
     * Method responsible for fetching the list of cruise groups for a given company
     * @param companyName company name
     * @return list of cruise group DTOs
     * @throws BaseAppException base application exception
     */
    List<CruiseGroupWithDetailsDto> getCruiseGroupForBusinessWorker(String companyName) throws BaseAppException;

    /**
     * Retrieves cruise group metadata
     *
     * @param uuid UUID of the cruise group selected for metadata
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    MetadataDto getCruiseGroupMetadata(UUID uuid) throws BaseAppException;
}
