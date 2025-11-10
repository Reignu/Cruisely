package com.cruisely.cruise.facades;


import com.cruisely.common.facades.AbstractFacade;
import com.cruisely.entities.cruise.Company;
import com.cruisely.entities.cruise.Cruise;
import com.cruisely.entities.cruise.CruiseGroup;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.cruise.endpoints.CruiseEndpoint;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Stateless
@Interceptors(TrackingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CruiseGroupFacadeMow extends AbstractFacade<CruiseGroup> {

    @PersistenceContext(unitName = "#")
    private EntityManager em;

    public CruiseGroupFacadeMow() {
        super(CruiseGroup.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    @Override
    public List<CruiseGroup> findAll() throws FacadeException {
        return super.findAll();
    }

    @Override
    @RolesAllowed({"changeCruiseGroup", "deactivateCruiseGroup", "removeClientRating","createRating"})
    public void edit(CruiseGroup entity) throws FacadeException {
        super.edit(entity);
    }

    @Override
    @RolesAllowed("addCruiseGroup")
    public void create(CruiseGroup entity) throws FacadeException {
        super.create(entity);
    }

    @PermitAll
    public CruiseGroup findByName(String name) throws BaseAppException {
        TypedQuery<CruiseGroup> tq = em.createNamedQuery("CruiseGroup.findByName", CruiseGroup.class);
        tq.setParameter("name", name);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @PermitAll
    public CruiseGroup findByUUID(UUID uuid) throws BaseAppException {
        TypedQuery<CruiseGroup> tq = em.createNamedQuery("CruiseGroup.findByUUID", CruiseGroup.class);
        tq.setParameter("uuid", uuid);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

   @RolesAllowed("getAllCruiseGroupList")
    public List<Cruise> findCruisesForCruiseGroup(CruiseGroup cruiseGroup) throws FacadeException {
        TypedQuery<Cruise> tq = em.createNamedQuery("CruiseGroup.findCruises", Cruise.class);
        tq.setParameter("name", cruiseGroup);
        try {
            return tq.getResultList();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }

    }
    @RolesAllowed("getCruiseGroupForBusinessWorker")
    public List<CruiseGroup> getCruiseGroupForBusinessWorker(Company company) throws FacadeException {
        TypedQuery<CruiseGroup> tq = em.createNamedQuery("CruiseGroup.findForBusinessWorker", CruiseGroup.class);
        tq.setParameter("name", company);
        try {
            return tq.getResultList();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }

    }

}