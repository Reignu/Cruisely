package com.cruisely.auth.facades;

import com.cruisely.common.facades.AbstractFacade;
import com.cruisely.entities.common.wrappers.TokenWrapper;
import com.cruisely.entities.auth.Account;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.FacadeException;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors(TrackingInterceptor.class)
public class TokenWrapperFacade extends AbstractFacade<TokenWrapper> {
    @PersistenceContext(unitName = "ssbd03mokPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TokenWrapperFacade() {
        super(TokenWrapper.class);
    }

    @PermitAll
    public List<TokenWrapper> getUsedToken() {
        TypedQuery<TokenWrapper> tq = em.createNamedQuery("TokenWrapper.findUsed", TokenWrapper.class);
        return tq.getResultList();
    }

    @PermitAll
    public List<TokenWrapper> getUnusedToken() {
        TypedQuery<TokenWrapper> tq = em.createNamedQuery("TokenWrapper.findUnused", TokenWrapper.class);
        return tq.getResultList();
    }

    @PermitAll
    public TokenWrapper findByToken(String token) throws BaseAppException {
        TypedQuery<TokenWrapper> tq = em.createNamedQuery("TokenWrapper.findByToken", TokenWrapper.class);
        tq.setParameter("token", token);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @PermitAll
    @Override
    public void create(TokenWrapper entity) throws FacadeException {
        super.create(entity);
    }

    @PermitAll
    @Override
    public void edit(TokenWrapper entity) throws FacadeException {
        super.edit(entity);
    }

    @PermitAll
    @Override
    public void remove(TokenWrapper entity) throws FacadeException {
        super.remove(entity);
    }

    @PermitAll
    public List<TokenWrapper> findByAccount(Account acc) throws FacadeException {
        TypedQuery<TokenWrapper> tq = em.createNamedQuery("TokenWrapper.findByAccount", TokenWrapper.class);
        tq.setParameter("account", acc);
        try {
            return tq.getResultList();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }
}
