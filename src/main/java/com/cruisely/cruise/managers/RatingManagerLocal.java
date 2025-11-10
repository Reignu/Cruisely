package com.cruisely.cruise.managers;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.cruisely.entities.cruise.Rating;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.FacadeException;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

@Local
public interface RatingManagerLocal {

    /**
     * Creates a rating by assigning the appropriate user and cruise group to it
     *
     * @param cruiseGroupUUID UUID of the cruise group for which the rating will be created
     * @param rating          rating value
     * @throws BaseAppException base application exception, returned when the user or cruise group is not found
     */
    void createRating(UUID cruiseGroupUUID, Double rating) throws BaseAppException;

    /**
     * Removes a rating for the cruise group with the given UUID
     *
     * @param cruiseGroupUUID UUID of the cruise group whose rating will be removed
     * @throws BaseAppException base application exception, returned when the cruise group, user or rating is not found
     */
    void removeRating(UUID cruiseGroupUUID) throws BaseAppException;

    /**
     * Retrieves the client's own ratings
     *
     * @throws BaseAppException base application exception, returned when no rating is found
     */
    List<Rating> getOwnRatings() throws BaseAppException;

    /**
     * Retrieves all ratings of a client
     *
     * @param login client's login
     * @return list of ratings
     * @throws BaseAppException base application exception
     */
    List<Rating> getClientRatings(String login) throws BaseAppException;

    /**
     * Removes a client's rating reported by a moderator
     *
     * @param login           client's login
     * @param cruiseGroupUUID UUID of the cruise group to which the rating belongs
     * @throws BaseAppException base application exception
     */
    void removeClientRating(String login, UUID cruiseGroupUUID) throws BaseAppException;

    /**
     * Returns a rating with the given UUID
     *
     * @param uuid UUID of the rating
     * @throws BaseAppException base application exception
     */
    Rating findByUuid(UUID uuid) throws FacadeException;

}
