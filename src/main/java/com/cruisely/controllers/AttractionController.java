package com.cruisely.controllers;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.ControllerException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.cruise.dto.attractions.AddAttractionDto;
import com.cruisely.cruise.dto.attractions.AttractionDto;
import com.cruisely.cruise.dto.attractions.EditAttractionDto;
import com.cruisely.cruise.dto.cruises.RelatedCruiseDto;
import com.cruisely.cruise.endpoints.AttractionEndpointLocal;
import com.cruisely.security.EntityIdentitySignerVerifier;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.MAPPER_UUID_PARSE;
import static com.cruisely.common.IntegrityUtils.checkEtagIntegrity;
import static com.cruisely.utils.TransactionRepeater.tryAndRepeat;

@Path("/attractions")
@RequestScoped
public class AttractionController {

    @Inject
    AttractionEndpointLocal attractionEndpoint;

    @GET
    @Path("/cruise/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AttractionDto> getAttractionsByCruiseUUID(@PathParam("uuid") String attractionUUID) throws BaseAppException {
        try {
            UUID uuid = UUID.fromString(attractionUUID);
            return tryAndRepeat(attractionEndpoint, () -> attractionEndpoint.getAttractionsByCruiseUUID(uuid));
        } catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

    /**
     * Method allowing adding an attraction to a cruise
     *
     * @param addAttractionDto DTO representation of the attraction
     * @return UUID of the created attraction
     * @throws BaseAppException Base application exception that may occur in case of business rule violations.
     */
    @POST
    @Path("/add-attraction")
    @Consumes(MediaType.APPLICATION_JSON)
    public UUID addAttraction(@Valid AddAttractionDto addAttractionDto) throws BaseAppException {
        return tryAndRepeat(attractionEndpoint, () -> attractionEndpoint.addAttraction(addAttractionDto));
    }



    @PUT
    @Path("/edit-attraction")
    public void editAttraction(@Valid EditAttractionDto editAttractionDto,
                               @HeaderParam("If-Match") String etag) throws BaseAppException {

        checkEtagIntegrity(editAttractionDto, etag);
        tryAndRepeat(attractionEndpoint, () -> attractionEndpoint.editAttraction(editAttractionDto));
    }

    /**
     * Method allowing removal of a given attraction from a cruise that has not been published yet
     *
     * @param uuid UUID of the attraction selected for removal
     * @throws BaseAppException Base application exception that may occur in case of business rule violations.
     */
    @DELETE
    @Path("/delete-attraction/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteAttraction(@PathParam("uuid") String uuid) throws BaseAppException {
     try{
         UUID convertedUUID = UUID.fromString(uuid);
         tryAndRepeat(attractionEndpoint, () -> attractionEndpoint.deleteAttraction(convertedUUID));
     } catch (IllegalArgumentException e) {
         throw new MapperException(MAPPER_UUID_PARSE);
     }
    }

    /**
     * Retrieves attraction metadata
     *
     * @param uuid UUID of the attraction selected for metadata
     * @return DTO representation of the metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getAttractionMetadata(@PathParam("uuid") String uuid) throws BaseAppException {
        try{
            UUID convertedUUID = UUID.fromString(uuid);
            return tryAndRepeat(attractionEndpoint, () -> attractionEndpoint.getAttractionMetadata(convertedUUID));
        }catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

}
