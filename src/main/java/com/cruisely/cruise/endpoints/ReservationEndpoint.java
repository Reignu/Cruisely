package com.cruisely.cruise.endpoints;

import com.cruisely.common.dto.MetadataDto;
import com.cruisely.common.endpoints.BaseEndpoint;
import com.cruisely.common.mappers.MetadataMapper;
import com.cruisely.entities.cruise.Reservation;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.MapperException;
import com.cruisely.cruise.dto.reservations.*;
import com.cruisely.cruise.endpoints.converters.ReservationMapper;
import com.cruisely.cruise.managers.ReservationManagerLocal;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.CRUISE_MAPPER_UUID_PARSE;
import static com.cruisely.common.I18n.RESERVATION_MAPPER_UUID_PARSE;

/**
 * Class for combining mapped dto class objects to reservation class objects, also invokes logic method that passes the mapped objects
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(TrackingInterceptor.class)
public class ReservationEndpoint extends BaseEndpoint implements ReservationEndpointLocal {

    @Inject
    private ReservationManagerLocal reservationManager;


    @Override
    @RolesAllowed("viewCruiseReservations")
    public List<CruiseReservationDto> viewCruiseReservations(UUID cruise_uuid) throws BaseAppException {
        List<CruiseReservationDto> res = new ArrayList<>();
        for (Reservation reservation : reservationManager.getCruiseReservations(cruise_uuid)) {
            res.add(ReservationMapper.toReservationDto(reservation));
        }
        return res;
    }

    @RolesAllowed("getWorkerCruiseReservations")
    @Override
    public List<CruiseReservationDto> viewWorkerCruiseReservations(UUID cruise_uuid) throws BaseAppException {
        List<CruiseReservationDto> res = new ArrayList<>();
        for (Reservation reservation : reservationManager.getWorkerCruiseReservations(cruise_uuid)) {
            res.add(ReservationMapper.toReservationDto(reservation));
        }
        return res;
    }

    @RolesAllowed("removeClientReservation")
    @Override
    public void removeClientReservation(RemoveClientReservationDto removeClientReservationDto) throws BaseAppException {
        try {
            this.reservationManager.removeClientReservation(UUID.fromString(removeClientReservationDto.getReservationUuid()), removeClientReservationDto.getClientLogin());
        } catch (IllegalArgumentException e) {
            throw new MapperException(RESERVATION_MAPPER_UUID_PARSE);
        }
    }

    @RolesAllowed("createReservation")
    @Override
    public void createReservation(CreateReservationDto crDto) throws BaseAppException {
        this.reservationManager.createReservation(crDto.getCruiseVersion(), crDto.getCruiseUuid(), crDto.getNumberOfSeats(), crDto.getAttractionsUUID());
    }

    @RolesAllowed("cancelReservation")
    @Override
    public void cancelReservation(UUID reservationUUID) throws BaseAppException {
        this.reservationManager.cancelReservation(reservationUUID);
    }

    @RolesAllowed("viewSelfReservations")
    @Override
    public List<SelfReservationDto> viewSelfCruiseReservations() throws BaseAppException {
        List<SelfReservationDto> res = new ArrayList<>();
        for (Reservation reservation : reservationManager.getClientReservations()) {
            res.add(ReservationMapper.toSelfReservationDto(reservation));
        }
        return res;
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public MetadataDto getReservationMetadata(UUID uuid) throws BaseAppException {
        return MetadataMapper.toMetadataDto(reservationManager.findByUUID(uuid));
    }
}
