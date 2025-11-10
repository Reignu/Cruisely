package com.cruisely.cruise.managers;

import com.cruisely.entities.cruise.Attraction;
import com.cruisely.exceptions.BaseAppException;

import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

/**
 * Class that manages business logic for attractions
 */
@Local
public interface AttractionManagerLocal {


    /**
     * Method responsible for removing an attraction
     *
     * @param uuid UUID of the attraction
     * @throws BaseAppException Base application exception
     */
    void deleteAttraction(UUID uuid) throws BaseAppException;


    /**
     * Method responsible for creating an attraction and adding it to a cruise group.
     *
     * @param attraction Object representing the attraction
     * @param cruiseUUID UUID of the cruise for which the attraction will be created
     * @return UUID of the newly created attraction
     * @throws BaseAppException Exception thrown in case of business rule violations
     */
    UUID addAttraction(Attraction attraction, UUID cruiseUUID) throws BaseAppException;


    /**
     * Method responsible for editing an existing attraction.
     *
     * @param attractionUUID   UUID of the attraction
     * @param newName          New name of the attraction
     * @param newDescription   New description of the attraction
     * @param newPrice         New price of the attraction
     * @param newNumberOfSeats New number of seats for the attraction
     * @param version          Version of the provided attraction
     * @throws BaseAppException Exception thrown when the attraction is not found or business rules are violated
     */
    void editAttraction(UUID attractionUUID, String newName, String newDescription, double newPrice, int newNumberOfSeats, long version) throws BaseAppException;

    /**
     * Retrieves attractions of a cruise by the given UUID
     *
     * @param uuid UUID of the cruise
     * @throws BaseAppException exception thrown when attractions are not found
     */
    List<Attraction> findByCruiseUUID(UUID uuid) throws BaseAppException;

    /**
     * Retrieves an attraction by its UUID
     *
     * @param uuid UUID of the attraction
     * @return attraction information
     * @throws BaseAppException exception thrown when the attraction is not found
     */
    Attraction findByUUID(UUID uuid) throws BaseAppException;


}
