package com.cruisely.cruise.managers;

import com.cruisely.entities.auth.Account;
import com.cruisely.entities.cruise.*;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.FacadeException;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

/**
 * Class that manages business logic for cruise groups
 */
@Local
public interface CruiseGroupManagerLocal {
    /**
     * Creates a new cruise group
     *
     * @param companyName     name of company creating the cruise group
     * @param name            cruise group name
     * @param number_of_seats number of seats for cruises in the cruise group
     * @param price           base price of cruises in the cruise group
     * @param start_address   starting address of the cruise group
     * @param pictures        list of photos associated with the cruise group
     * @throws BaseAppException Base application exception
     */
    void addCruiseGroup(String companyName, String name, long number_of_seats, Double price, CruiseAddress start_address, List<CruisePicture> pictures,String description) throws BaseAppException;

    /**
     * Edits the given cruise group
     *
     * @param name            new cruise name
     * @param number_of_seats new number of seats
     * @param price           new price
     * @param start_address   new address of cruise group
     * @param version         version for optimistic locking
     * @return modified CruiseGroup class object
     * @throws BaseAppException Base application exception
     */
    void changeCruiseGroup(String name, long number_of_seats, Double price, CruiseAddress start_address, long version,String description, CruisePicture picture, UUID uuid) throws BaseAppException;


    /**
     * Retrieves all cruise group objects from database
     *
     * @return all cruise groups
     */
    List<CruiseGroup> getAllCruiseGroups() throws FacadeException;

    /**
     * Method returning active user for mow
     * @return Account of active user
     * @throws BaseAppException Base application exception
     */
   Account getCurrentUser() throws BaseAppException;

        /**
         * Method responsible for deactivating cruise group
         *
         * @param uuid uuid of cruise group to deactivate along with version
         * @param version version of cruise group to deactivate
         * @throws BaseAppException Base application exception
         */
    void deactivateCruiseGroup(UUID uuid, long version) throws BaseAppException;

    /**
     * Retrieves cruises for a given cruise group
     * @param cruiseGroup cruise group
     * @return  list of cruises assigned to the given cruise group
     * @throws BaseAppException base application exception
     */
    List<Cruise> getCruiseBelongsToCruiseGroup(CruiseGroup cruiseGroup) throws BaseAppException;

    /**
     * Retrieves the list of cruise groups belonging to a given company
     * @param companyName   name of company for which cruise groups should be retrieved
     * @return  list of cruise groups for the given company
     * @throws BaseAppException base application exception
     */
    List<CruiseGroup> getCruiseGroupForBusinessWorker(String companyName) throws BaseAppException;

    /**
     * Retrieves cruise group with given uuid
     *
     * @param uuid uuid of cruise group
     * @return returns information about cruise group
     * @throws BaseAppException exception thrown when attraction is not found
     */
    CruiseGroup findByUUID(UUID uuid) throws BaseAppException;
}
