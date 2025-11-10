package com.cruisely.cruise.facades;

import com.cruisely.common.facades.AbstractFacade;
import com.cruisely.entities.cruise.Cruise;
import com.cruisely.entities.cruise.Reservation;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors(TrackingInterceptor.class)
public class ReservationFacadeMow extends AbstractFacade<Reservation> {

    @PersistenceContext(unitName = "ssbd03mowPU")
    private EntityManager em;

    public ReservationFacadeMow() {
        super(Reservation.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    @Override
    @RolesAllowed("createReservation")
    public void create(Reservation entity) throws FacadeException {
        super.create(entity);
    }

    @RolesAllowed("authenticatedUser")
    public Reservation findByUUID(UUID uuid) throws BaseAppException {
        TypedQuery<Reservation> tq = em.createNamedQuery("Reservation.findByUUID", Reservation.class);
        tq.setParameter("uuid", uuid);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @RolesAllowed({"getWorkerCruiseReservations", "viewCruiseReservations", "createReservation"})
    public List<Reservation> findCruiseReservations(Cruise cruise) throws BaseAppException {
        TypedQuery<Reservation> tq = em.createNamedQuery("Reservation.findCruiseReservations", Reservation.class);
        tq.setParameter("uuid", cruise.getUuid());
        try {
            return tq.getResultList();
        } catch (PersistenceException exp) {
            throw FacadeException.databaseOperation();
        }
    }

    @RolesAllowed({"removeClientReservation", "cancelReservation"})
    public Reservation findReservationByUuidAndLogin(UUID uuid, String login) throws BaseAppException {
        TypedQuery<Reservation> tq = em.createNamedQuery("Reservation.findByUUIDAndLogin", Reservation.class);
        tq.setParameter("uuid", uuid);
        tq.setParameter("login", login);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @RolesAllowed("viewSelfReservations")
    public List<Reservation> findReservationByLogin(String login) throws BaseAppException {
        TypedQuery<Reservation> tq = em.createNamedQuery("Reservation.findByLogin", Reservation.class);
        tq.setParameter("login", login);
        try {
            return tq.getResultList();
        } catch (PersistenceException exp) {
            throw FacadeException.databaseOperation();
        }
    }

    @RolesAllowed({"removeClientReservation", "cancelReservation"})
    @Override
    public void remove(Reservation entity) throws FacadeException {
        super.remove(entity);
    }
}