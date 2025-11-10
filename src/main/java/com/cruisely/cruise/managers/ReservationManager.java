package com.cruisely.cruise.managers;

import com.cruisely.entities.auth.AccessLevelType;
import com.cruisely.entities.auth.Account;
import com.cruisely.entities.auth.accesslevels.BusinessWorker;
import com.cruisely.entities.auth.accesslevels.Client;
import com.cruisely.entities.cruise.Attraction;
import com.cruisely.entities.cruise.Cruise;
import com.cruisely.entities.cruise.Reservation;
import com.cruisely.exceptions.*;
import com.cruisely.auth.endpoints.converters.AccountMapper;
import com.cruisely.cruise.facades.AttractionFacadeMow;
import com.cruisely.cruise.facades.CruiseFacadeMow;
import com.cruisely.cruise.facades.ReservationFacadeMow;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cruisely.common.I18n.*;
import static com.cruisely.common.IntegrityUtils.checkForOptimisticLock;

/**
 * Class that manages business logic for reservations
 */

@Stateful
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors(TrackingInterceptor.class)
public class ReservationManager extends BaseManagerMow implements ReservationManagerLocal {

    @Inject
    private ReservationFacadeMow reservationFacadeMow;

    @Inject
    private AttractionFacadeMow attractionFacadeMow;

    @Inject
    private CruiseFacadeMow cruiseFacadeMow;

    @Override
    @RolesAllowed("viewCruiseReservations")
    public List<Reservation> getCruiseReservations(UUID cruise_uuid) throws BaseAppException {
        Cruise cruise = cruiseFacadeMow.findByUUID(cruise_uuid);
        return reservationFacadeMow.findCruiseReservations(cruise);
    }

    @RolesAllowed("getWorkerCruiseReservations")
    @Override
    public List<Reservation> getWorkerCruiseReservations(UUID cruise_uuid) throws BaseAppException {
        Cruise cruise = cruiseFacadeMow.findByUUID(cruise_uuid);
        Account account = getCurrentUser();

        BusinessWorker businessWorker = (BusinessWorker) AccountMapper.getAccessLevel(account, AccessLevelType.BUSINESS_WORKER);

        if (cruise.getCruisesGroup().getCompany().getNIP() != businessWorker.getCompany().getNIP()) {
            throw new CruiseManagerException(NOT_YOURS_CRUISE);
        }

        return reservationFacadeMow.findCruiseReservations(cruise);
    }

    @RolesAllowed("removeClientReservation")
    @Override
    public void removeClientReservation(UUID reservationUuid, String clientLogin) throws BaseAppException {
        Reservation reservation = reservationFacadeMow.findReservationByUuidAndLogin(reservationUuid, clientLogin);
        reservationFacadeMow.remove(reservation);
    }


    @RolesAllowed("createReservation")
    @Override
    public void createReservation(long version, UUID cruiseUUID, long numberOfSeats, List<String> attractionsUUID) throws BaseAppException {
        Cruise cruise = cruiseFacadeMow.findByUUID(cruiseUUID);
        checkForOptimisticLock(cruise, version);

        Account account = getCurrentUser();
        Client client = (Client) AccountMapper.getAccessLevel(account, AccessLevelType.CLIENT);

        if (numberOfSeats > getAvailableSeats(cruiseUUID)) {
            throw new NoSeatsAvailableException(NO_SEATS_AVAILABLE);
        }

        if (cruise.getStartDate().isBefore(LocalDateTime.now())) {
            throw new ReservationManagerException(CANNOT_BOOK_STARTED_CRUISE);
        }

        List<Attraction> attractions = new ArrayList<>();

        for (String uuidStr : attractionsUUID) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Attraction attraction = attractionFacadeMow.findByUUID(uuid);
                long allSeats = attraction.getNumberOfSeats();
                long takenSeats = attractionFacadeMow.getNumberOfTakenSeats(attraction);
                if (allSeats < takenSeats + numberOfSeats) {
                    throw new ReservationManagerException(NO_SEATS_FOR_ATTRACTION);
                }
                attractions.add(attraction);
            } catch (IllegalArgumentException e) {
                throw new MapperException(MAPPER_UUID_PARSE);
            }
        }

        Reservation reservation = new Reservation(numberOfSeats, cruise, cruise.getCruisesGroup().getPrice() * numberOfSeats, client);
        reservation.getAttractions().addAll(attractions);

        setCreatedMetadata(account, reservation);
        reservationFacadeMow.create(reservation);
    }

    @RolesAllowed("cancelReservation")
    @Override
    public void cancelReservation(UUID reservationUUID) throws BaseAppException {
        String login = getCurrentUser().getLogin();
        Reservation reservation = reservationFacadeMow.findReservationByUuidAndLogin(reservationUUID, login);

        if (reservation.getCruise().getStartDate().isBefore(LocalDateTime.now())) {
            throw new FacadeException(CANNOT_CANCEL_STARTED_CRUISE);
        }

        reservationFacadeMow.remove(reservation);
    }

    private long getAvailableSeats(UUID cruiseUUID) throws BaseAppException {
        Cruise cruise = cruiseFacadeMow.findByUUID(cruiseUUID);
        List<Reservation> reservations = reservationFacadeMow.findCruiseReservations(cruise);

        long reservedSeats = reservations.stream().mapToLong(Reservation::getNumberOfSeats).sum();
        long allSeats = cruise.getCruisesGroup().getNumberOfSeats();

        return allSeats - reservedSeats;
    }

    @RolesAllowed("viewSelfReservations")
    @Override
    public List<Reservation> getClientReservations() throws BaseAppException {
        Account account = getCurrentUser();
        return reservationFacadeMow.findReservationByLogin(account.getLogin());
    }

    @RolesAllowed("authenticatedUser")
    @Override
    public Reservation findByUUID(UUID uuid) throws BaseAppException {
        return reservationFacadeMow.findByUUID(uuid);
    }
}
