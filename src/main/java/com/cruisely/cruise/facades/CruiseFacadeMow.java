package com.cruisely.cruise.facades;

import com.cruisely.common.facades.AbstractFacade;
import com.cruisely.entities.cruise.Cruise;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors(TrackingInterceptor.class)
public class CruiseFacadeMow extends AbstractFacade<Cruise> {

    @PersistenceContext(unitName = "#")
    private EntityManager em;

    public CruiseFacadeMow() {
        super(Cruise.class);
    }

    @Override
    @RolesAllowed({"editCruise","publishCruise"})
    public void edit(Cruise entity) throws FacadeException {
        super.edit(entity);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public Cruise findByUUID(UUID uuid) throws BaseAppException {
        TypedQuery<Cruise> tq = em.createNamedQuery("Cruise.findByUUID", Cruise.class);
        tq.setParameter("uuid", uuid);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @PermitAll
    public List<Cruise> getPublishedCruises() throws BaseAppException {
        TypedQuery<Cruise> tq = em.createNamedQuery("Cruise.findAllPublished", Cruise.class);
        try {
            return tq.getResultList();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }


    @PermitAll
    public List<Cruise> findByCruiseGroupUUID(UUID uuid) throws BaseAppException {
        TypedQuery<Cruise> tq = em.createNamedQuery("Cruise.findByCruiseGroupUUID", Cruise.class);
        tq.setParameter("uuid", uuid);

        try {
            return tq.getResultList();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @RolesAllowed("addCruise")
    @Override
    public void create(Cruise entity) throws FacadeException {
        super.create(entity);
    }
}
