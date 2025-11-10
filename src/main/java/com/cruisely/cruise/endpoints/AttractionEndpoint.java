package com.cruisely.cruise.endpoints;


import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.BaseEndpoint;
import com.cruisely.common.mappers.MetadataMapper;
import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.auth.accesslevels.Client;
import com.cruisely.entities.cruise.Attraction;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.cruise.dto.attractions.AddAttractionDto;
import com.cruisely.cruise.dto.attractions.AttractionDto;
import com.cruisely.cruise.dto.attractions.EditAttractionDto;
import com.cruisely.cruise.endpoints.converters.AttractionMapper;
import com.cruisely.cruise.managers.AttractionManagerLocal;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.MAPPER_UUID_PARSE;

/**
 * Class that handles collecting mapped DTO objects to model objects related to attractions and calls logic methods passing mapped objects.
 */

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(TrackingInterceptor.class)
public class AttractionEndpoint extends BaseEndpoint implements AttractionEndpointLocal {

    @Inject
    private AttractionManagerLocal attractionManager;

    @RolesAllowed("deleteAttraction")
    @Override
    public void deleteAttraction(UUID uuid) throws BaseAppException {
        this.attractionManager.deleteAttraction(uuid);
    }


    @RolesAllowed("addAttraction")
    @Override
    public UUID addAttraction(AddAttractionDto addAttractionDto) throws BaseAppException {
        try {
            UUID cruiseUUID = UUID.fromString(addAttractionDto.getCruiseUUID());
            return attractionManager.addAttraction(AttractionMapper.toAttraction(addAttractionDto), cruiseUUID);
        } catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

    @RolesAllowed("editAttraction")
    @Override
    public void editAttraction(EditAttractionDto editAttractionDto) throws BaseAppException {
        try {
            UUID cruiseUUID = UUID.fromString(editAttractionDto.getUuid());
            attractionManager.editAttraction(cruiseUUID, editAttractionDto.getNewName(), editAttractionDto.getNewDescription(),
                    editAttractionDto.getNewPrice(), editAttractionDto.getNewNumberOfSeats(), editAttractionDto.getVersion());
        } catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

    @PermitAll
    @Override
    public List<AttractionDto> getAttractionsByCruiseUUID(UUID uuid) throws BaseAppException {
        List<AttractionDto> attractions = new ArrayList<>();
        for (Attraction attraction : attractionManager.findByCruiseUUID(uuid)) {
            attractions.add(AttractionMapper.toAttractionDto(attraction));
        }
        return attractions;
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public MetadataDto getAttractionMetadata(UUID uuid) throws BaseAppException {
        return MetadataMapper.toMetadataDto(attractionManager.findByUUID(uuid));
    }


}