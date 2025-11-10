package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.TransactionalEndpoint;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.cruise.dto.ratings.ClientRatingDto;
import com.cruisely.cruise.dto.CreateRatingDto;
import com.cruisely.cruise.dto.ratings.RatingDto;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

@Local
public interface RatingEndpointLocal extends TransactionalEndpoint {

    /**
     * Adds a rating for a user with the given login for the cruise with the given id
     *
     * @param ratingDto DTO object holding information to create the rating
     * @throws BaseAppException base application exception, returned when the user or cruise group is not found
     */
    void createRating(CreateRatingDto ratingDto) throws BaseAppException;

    /**
     * Removes a rating for the specified cruise group
     *
     * @param cruiseGroupUUID UUID of the cruise group
     * @throws BaseAppException base application exception, returned when the user or cruise group is not found
     */
    void removeRating(UUID cruiseGroupUUID) throws BaseAppException;

    /**
     * Retrieves all ratings of a client
     * @param login client's login
     * @return list of ratings
     * @throws BaseAppException base application exception
     */
    List<ClientRatingDto> getClientRatings(String login) throws BaseAppException;

    /**
     * Retrieves the client's ratings (own ratings)
     *
     * @throws BaseAppException base application exception, returned when no rating is found
     */
    List<RatingDto> getOwnRatings() throws BaseAppException;

    /**
     * Method used to remove a client's rating by a moderator
     * @param login client's login
     * @param cruiseGroupUUID UUID of the cruise group
     * @throws BaseAppException
     */
    void removeClientRating(String login, UUID cruiseGroupUUID) throws BaseAppException;

    /**
     * Retrieves metadata for a rating with the given UUID
     *
     * @param uuid UUID of the rating
     * @return returns information about the rating
     * @throws BaseAppException exception thrown when not found
     */
    MetadataDto getRatingMetadata(UUID uuid) throws BaseAppException;
}
