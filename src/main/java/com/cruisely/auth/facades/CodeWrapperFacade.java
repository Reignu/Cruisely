package com.cruisely.auth.facades;

import com.cruisely.common.facades.AbstractFacade;
import com.cruisely.entities.common.wrappers.CodeWrapper;
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
public class CodeWrapperFacade extends AbstractFacade<CodeWrapper> {
    @PersistenceContext(unitName = "ssbd03mokPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CodeWrapperFacade() {
        super(CodeWrapper.class);
    }

    @PermitAll
    public List<CodeWrapper> getUsedCode() {
        TypedQuery<CodeWrapper> tq = em.createNamedQuery("CodeWrapper.findUsed", CodeWrapper.class);
        return tq.getResultList();
    }

    @PermitAll
    public List<CodeWrapper> getUnusedCode() {
        TypedQuery<CodeWrapper> tq = em.createNamedQuery("CodeWrapper.findUnused", CodeWrapper.class);
        return tq.getResultList();
    }

    @PermitAll
    public CodeWrapper findByCode(String code) throws BaseAppException {
        TypedQuery<CodeWrapper> tq = em.createNamedQuery("CodeWrapper.findByCode", CodeWrapper.class);
        tq.setParameter("code", code);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @PermitAll
    @Override
    public void create(CodeWrapper entity) throws FacadeException {
        super.create(entity);
    }

    @PermitAll
    @Override
    public void edit(CodeWrapper entity) throws FacadeException {
        super.edit(entity);
    }

    @PermitAll
    @Override
    public void remove(CodeWrapper entity) throws FacadeException {
        super.remove(entity);
    }
}
