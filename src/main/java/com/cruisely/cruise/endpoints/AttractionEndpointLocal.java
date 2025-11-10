package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.TransactionalEndpoint;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.cruise.dto.attractions.AddAttractionDto;
import com.cruisely.cruise.dto.attractions.AttractionDto;
import com.cruisely.cruise.dto.attractions.EditAttractionDto;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

/**
 * Interface that collects mapped DTO objects into model objects related to attractions
 * and invokes business logic methods with the mapped objects.
 */
@Local
public interface AttractionEndpointLocal extends TransactionalEndpoint {

    /**
     * Method responsible for invoking the logic to remove an attraction.
     *
     * @param uuid UUID of the attraction to remove
     * @throws BaseAppException
     */
    void deleteAttraction(UUID uuid) throws BaseAppException;

    /**
     * Method responsible for creating an attraction and adding it to a cruise group.
     *
     * @param addAttractionDto DTO object representing the attraction
     * @return UUID of the newly created attraction
     * @throws BaseAppException Exception thrown in case of business rule violations
     */
    UUID addAttraction(AddAttractionDto addAttractionDto) throws BaseAppException;

    /**
     * Method responsible for editing an existing attraction
     *
     * @param editAttractionDto DTO object representing the attraction with changes
     * @throws BaseAppException Exception thrown when the attraction is not found or business rules are violated
     */
    void editAttraction(EditAttractionDto editAttractionDto) throws BaseAppException;

    /**
     * Retrieves attractions of a cruise by the given UUID
     *
     * @param uuid UUID of the cruise
     * @return list of attractions
     * @throws BaseAppException exception thrown when attractions are not found
     */
    List<AttractionDto> getAttractionsByCruiseUUID(UUID uuid) throws BaseAppException;

    /**
     * Retrieves metadata for an attraction by the given UUID
     *
     * @param uuid UUID of the attraction
     * @return metadata information for the attraction
     * @throws BaseAppException exception thrown when the attraction is not found
     */
    MetadataDto getAttractionMetadata(UUID uuid) throws BaseAppException;
}
