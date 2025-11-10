package com.cruisely.cruise.facades;

import com.cruisely.common.facades.AbstractFacade;
import com.cruisely.entities.cruise.Rating;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

import static javax.ejb.TransactionAttributeType.MANDATORY;

@TransactionAttribute(MANDATORY)
@Stateless
@Interceptors(TrackingInterceptor.class)
public class RatingFacadeMow extends AbstractFacade<Rating> {

    @PersistenceContext(unitName = "#")
    private EntityManager em;

    public RatingFacadeMow() {
        super(Rating.class);
    }

    public List<Rating> findByCruiseGroupUUID(UUID uuid) throws BaseAppException {
        TypedQuery<Rating> tq = em.createNamedQuery("Rating.findByCruiseGroupUUID", Rating.class);
        tq.setParameter("uuid", uuid);

        try {
            return tq.getResultList();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    public Rating findByCruiseGroupUUIDAndAccountLogin(UUID uuid, String login) throws BaseAppException {
        TypedQuery<Rating> tq = em.createNamedQuery("Rating.findByCruiseGroupUUIDAndAccountLogin", Rating.class);
        tq.setParameter("uuid", uuid);
        tq.setParameter("login", login);

        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    public Long countByLogin(String login, UUID uuid) throws BaseAppException {
        TypedQuery<Long> tq = em.createNamedQuery("Rating.countByCruiseGroupUUIDAndAccountLogin", Long.class);
        tq.setParameter("login", login);
        tq.setParameter("uuid", uuid);

        return tq.getSingleResult();
    }

    public Rating findByCruiseUuidAndAccountLogin(String login, UUID uuid) throws BaseAppException {
        TypedQuery<Rating> tq = em.createNamedQuery("Rating.findByUuidAndAccountLogin", Rating.class);
        tq.setParameter("login", login);
        tq.setParameter("uuid", uuid);

        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @RolesAllowed("authenticatedUser")
    public Rating findRatingByUuid(UUID uuid) throws FacadeException {
        TypedQuery<Rating> tq = em.createNamedQuery("Rating.findByUuid", Rating.class);
        tq.setParameter("uuid", uuid);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    public List<Rating> findUserRatings(String login) throws BaseAppException {
        TypedQuery<Rating> tq = em.createNamedQuery("Rating.findUserRatings", Rating.class);
        tq.setParameter("login", login);
        try {
            return tq.getResultList();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @RolesAllowed({"removeRating", "removeClientRating"})
    @Override
    public void remove(Rating entity) throws FacadeException {
        super.remove(entity);
    }

    @RolesAllowed("createRating")
    @Override
    public void create(Rating entity) throws FacadeException {
        super.create(entity);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
