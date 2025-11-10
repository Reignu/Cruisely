package com.cruisely.controllers;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.cruise.dto.cruiseGroups.AddCruiseGroupDto;
import com.cruisely.cruise.dto.cruiseGroups.CruiseGroupWithDetailsDto;
import com.cruisely.cruise.dto.cruiseGroups.DeactivateCruiseGroupDto;
import com.cruisely.cruise.dto.cruiseGroups.ChangeCruiseGroupDto;
import com.cruisely.cruise.endpoints.CruiseGroupEndpointLocal;
import com.cruisely.security.ETagFilterBinding;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;
import static com.cruisely.common.IntegrityUtils.checkEtagIntegrity;
import static com.cruisely.utils.TransactionRepeater.tryAndRepeat;

@Path("/cruiseGroup")
@RequestScoped
public class CruiseGroupController {
    @Inject
    private CruiseGroupEndpointLocal cruiseGroupEndpoint;

    /**
     * Adds a cruise trip
     *  @param addCruiseGroupDto dto object allowing to create a cruise trip
     * @throws BaseAppException Base application exception
     */
    @POST
    @Path("/add-cuise-group")
    public void addCruiseGroup(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid AddCruiseGroupDto addCruiseGroupDto) throws BaseAppException {
        tryAndRepeat(cruiseGroupEndpoint, () -> cruiseGroupEndpoint.addCruiseGroup(addCruiseGroupDto));
    }

    /**
     * Get information about all cruise trip groups
     *
     * @return List of accounts
     */
    @GET
    @Path("/cruise-groups")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CruiseGroupWithDetailsDto> getAllCruiseGroups() throws BaseAppException {
        return tryAndRepeat(cruiseGroupEndpoint, () -> cruiseGroupEndpoint.getCruiseGroupsInfo());
    }

    /**
     * Method indirectly responsible for deactivating cruise trip groups
     *
     * @param deactivateCruiseGroupDto Object with uuid of cruise trip group to deactivate and version
     * @param etag                     ETag value
     * @throws BaseAppException Base application exception
     */
    @ETagFilterBinding
    @PUT
    @Path("/deactivate-cruise-group")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deactivateCruiseGroup(@Valid DeactivateCruiseGroupDto deactivateCruiseGroupDto, @HeaderParam("If-Match") @NotNull(message = CONSTRAINT_NOT_NULL) @NotEmpty(message = CONSTRAINT_NOT_EMPTY) @Valid String etag) throws BaseAppException {
        checkEtagIntegrity(deactivateCruiseGroupDto, etag);
        tryAndRepeat(cruiseGroupEndpoint, () -> cruiseGroupEndpoint.deactivateCruiseGroup(deactivateCruiseGroupDto.getUuid(), deactivateCruiseGroupDto.getVersion()));
    }

    /**
     * Method retrieving the list of cruise trip groups belonging to a given company
     * @param companyName company name
     * @return  list of cruise trip group dtos
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/CruiseGroupForBusinessWorker/{companyName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CruiseGroupWithDetailsDto> getCruiseGroupForBusinessWorker(@PathParam("companyName") @Valid String companyName) throws BaseAppException {
        return tryAndRepeat(cruiseGroupEndpoint, () -> cruiseGroupEndpoint.getCruiseGroupForBusinessWorker(companyName));
    }

    /**
     * Change data of selected cruise trip group
     *
     * @param dto  dto object with new data
     * @param etag If-Match header required to confirm data consistency
     */
    @PUT
    @Path("/change-cruise-group")
    @Consumes(MediaType.APPLICATION_JSON)
    @ETagFilterBinding
    public void changeCruiseGroupData(@NotNull(message = CONSTRAINT_NOT_NULL) @Valid ChangeCruiseGroupDto dto,
                                      @HeaderParam("If-Match") String etag) throws BaseAppException {
        checkEtagIntegrity(dto, etag);
        tryAndRepeat(cruiseGroupEndpoint, () -> cruiseGroupEndpoint.changeCruiseGroup(dto));
    }

    /**
     * Retrieves metadata of cruise trip group
     *
     * @param uuid UUID of cruise trip group selected for metadata
     * @return DTO representation of metadata
     * @throws BaseAppException Base application exception
     */
    @GET
    @Path("/metadata/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataDto getCruiseGroupMetadata(@PathParam("uuid") String uuid) throws BaseAppException {
        try{
            UUID convertedUUID = UUID.fromString(uuid);
            return tryAndRepeat(cruiseGroupEndpoint, () -> cruiseGroupEndpoint.getCruiseGroupMetadata(convertedUUID));
        }catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

}
