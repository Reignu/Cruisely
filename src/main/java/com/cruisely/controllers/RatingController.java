package com.cruisely.controllers;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.cruise.dto.CreateRatingDto;
import com.cruisely.cruise.dto.ratings.ClientRatingDto;
import com.cruisely.cruise.endpoints.RatingEndpointLocal;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.MAPPER_UUID_PARSE;
import static com.cruisely.utils.TransactionRepeater.tryAndRepeat;

@Path("/ratings")
@RequestScoped
public class RatingController {

    @Inject
    private RatingEndpointLocal ratingEndpoint;

    /**
     * Creates a new rating for a cruise
     *
     * @param ratingDto DTO object representing the rating
     *
     * @throws BaseAppException Base application exception
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void createRating(@Valid CreateRatingDto ratingDto) throws BaseAppException {
        tryAndRepeat(ratingEndpoint, () -> ratingEndpoint.createRating(ratingDto));
    }

    /**
     * Removes a rating for a cruise
     *
     * @param uuid UUID of the cruise group
     *
     * @throws BaseAppException Base application exception
     */
    @DELETE
    @Path("/{uuid}")
    public void removeRating(@PathParam("uuid") String uuid) throws  BaseAppException {
        tryAndRepeat(ratingEndpoint, () -> ratingEndpoint.removeRating(UUID.fromString(uuid)));
    }

    /**
     * Retrieves the list of ratings for a user
     *
     * @param clientLogin client's login
     *
     * @return List of user's ratings
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/{clientLogin}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClientRatingDto> getClientRatings(@PathParam("clientLogin") String clientLogin) throws BaseAppException {
        return tryAndRepeat(ratingEndpoint, () -> ratingEndpoint.getClientRatings(clientLogin));
    }

    /**
     * Removes a rating for a cruise for a given user
     *
     * @param uuid UUID of the cruise group
     * @param clientLogin user's login
     *
     * @throws BaseAppException Base application exception
     */
    @DELETE
    @Path("/{clientLogin}/{uuid}")
    public void removeClientRating(@PathParam("clientLogin") String clientLogin, @PathParam("uuid") String uuid) throws BaseAppException {
        tryAndRepeat(ratingEndpoint, () -> ratingEndpoint.removeClientRating(clientLogin, UUID.fromString(uuid)));
    }

    /**
     * Retrieves rating metadata
     *
     * @param uuid UUID of the rating selected for metadata
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getRatingMetadata(@PathParam("uuid") String uuid) throws BaseAppException {
        try{
            UUID convertedUUID = UUID.fromString(uuid);
            return tryAndRepeat(ratingEndpoint, () -> ratingEndpoint.getRatingMetadata(convertedUUID));
        }catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

}
