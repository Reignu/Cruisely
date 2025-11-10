package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.BaseEndpoint;
import com.cruisely.common.mappers.MetadataMapper;
import com.cruisely.entities.cruise.Cruise;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.cruise.dto.cruises.*;
import com.cruisely.cruise.endpoints.converters.CruiseGroupMapper;
import com.cruisely.cruise.endpoints.converters.CruiseMapper;
import com.cruisely.cruise.managers.CruiseManagerLocal;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.cruisely.common.I18n.MAPPER_UUID_PARSE;

/**
 * Class responsible for handling DTO objects related to cruises
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(TrackingInterceptor.class)
public class CruiseEndpoint extends BaseEndpoint implements CruiseEndpointLocal {

    @Inject
    private CruiseManagerLocal cruiseManager;


    @RolesAllowed("addCruise")
    @Override
    public void addCruise(NewCruiseDto newCruiseDto) throws BaseAppException {
        try {
            cruiseManager.addCruise(CruiseMapper.mapNewCruiseDtoToCruise(newCruiseDto), UUID.fromString(newCruiseDto.getCruiseGroupUUID()));
        } catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }

    }

    @RolesAllowed("deactivateCruise")
    @Override
    public void deactivateCruise(DeactivateCruiseDto deactivateCruiseDto) throws BaseAppException {
        try {
            cruiseManager.deactivateCruise(UUID.fromString(deactivateCruiseDto.getUuid()), deactivateCruiseDto.getVersion());
        } catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }

    }

    @PermitAll
    @Override
    public CruiseDto getCruise(UUID uuid) throws BaseAppException {
        Cruise cruise = cruiseManager.getCruise(uuid);

        return CruiseMapper.mapCruiseToCruiseDto(cruise);
    }

    @PermitAll
    @Override
    public List<RelatedCruiseDto> getCruisesByCruiseGroup(UUID uuid) throws BaseAppException {
        List<Cruise> cruises = cruiseManager.getCruisesByCruiseGroup(uuid);

        return cruises.stream().filter(Cruise::isActive).map(CruiseMapper::toRelatedCruiseDto).collect(Collectors.toList());
    }

    @RolesAllowed("publishCruise")
    @Override
    public void publishCruise(PublishCruiseDto publishCruiseDto) throws BaseAppException {
        try {
            cruiseManager.publishCruise(publishCruiseDto.getCruiseVersion(), UUID.fromString(publishCruiseDto.getCruiseUuid()));
        } catch (IllegalArgumentException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

    @RolesAllowed("editCruise")
    @Override
    public void editCruise(EditCruiseDto editCruiseDto) throws BaseAppException {
        try {
            cruiseManager.editCruise(LocalDateTime.ofInstant(Instant.parse(editCruiseDto.getStartDate()), ZoneId.systemDefault()),
                    LocalDateTime.ofInstant(Instant.parse(editCruiseDto.getEndDate()), ZoneId.systemDefault()),
                    editCruiseDto.getUuid(), editCruiseDto.getVersion());
        } catch (DateTimeParseException e) {
            throw new MapperException(MAPPER_UUID_PARSE);
        }
    }

    @PermitAll
    @Override
    public List<CruiseGroupWithCruisesDto> getPublishedCruises() throws BaseAppException {
        List<Cruise> cruises = cruiseManager.getPublishedCruises();
        return CruiseMapper.toListOfCruiseGroupsWithCruisesDto(cruises);
    }

    @PermitAll
    @Override
    public List<CruiseForCruiseGroupDto> getCruisesForCruiseGroup(UUID cruiseGroupUUID) throws BaseAppException {
        return CruiseGroupMapper.toCruiseForCruiseGroupDtos(cruiseManager.getCruisesByCruiseGroup(cruiseGroupUUID));
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public MetadataDto getCruiseMetadata(UUID uuid) throws BaseAppException {
        return MetadataMapper.toMetadataDto(cruiseManager.findByUUID(uuid));
    }

}
