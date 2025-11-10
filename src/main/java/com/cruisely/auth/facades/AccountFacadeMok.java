package com.cruisely.auth.facades;

import lombok.extern.java.Log;
import org.hibernate.exception.ConstraintViolationException;
import com.cruisely.common.facades.AbstractFacade;
import com.cruisely.entities.common.AlterType;
import com.cruisely.entities.common.wrappers.AlterTypeWrapper;
import com.cruisely.entities.auth.AccessLevel;
import com.cruisely.entities.auth.Account;
import com.cruisely.entities.auth.LanguageType;
import com.cruisely.entities.auth.wrappers.LanguageTypeWrapper;
import com.cruisely.exceptions.AccountFacadeException;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.FacadeException;
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

import static com.cruisely.entities.auth.Account.EMAIL_CONSTRAINT;
import static com.cruisely.entities.auth.Account.LOGIN_CONSTRAINT;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors(TrackingInterceptor.class)
public class AccountFacadeMok extends AbstractFacade<Account> {

    @PersistenceContext(unitName = "#")
    private EntityManager em;

    public AccountFacadeMok() {
        super(Account.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public AlterTypeWrapper getAlterTypeWrapperByAlterType(AlterType alterType) throws FacadeException {
        TypedQuery<AlterTypeWrapper> tq = em.createNamedQuery("AlterTypeWrapper.findByName", AlterTypeWrapper.class);
        tq.setParameter("name", alterType);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @PermitAll
    public LanguageTypeWrapper getLanguageTypeWrapperByLanguageType(LanguageType languageType) throws FacadeException {
        TypedQuery<LanguageTypeWrapper> tq = em.createNamedQuery("LanguageTypeWrapper.findByName", LanguageTypeWrapper.class);
        tq.setParameter("name", languageType);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw FacadeException.noSuchElement();
        }
    }

    @RolesAllowed("getAllAccounts")
    @Override
    public List<Account> findAll() throws FacadeException {
        return super.findAll();
    }

    @PermitAll
    public Account findByLogin(String login) throws BaseAppException {
        TypedQuery<Account> tq = em.createNamedQuery("Account.findByLogin", Account.class);
        tq.setParameter("login", login);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            throw AccountFacadeException.userNotExists(e);
        }
    }


    @PermitAll
    public List<Account> getUnconfirmedAccounts() {
        TypedQuery<Account> tqq = em.createNamedQuery("Account.findUnconfirmedAccounts", Account.class);
        return tqq.getResultList();
    }

    @RolesAllowed("authenticatedUser")
    public boolean isEmailPresent(String email) {
        TypedQuery<Account> tqq = em.createNamedQuery("Account.isEmailPresent", Account.class);
        tqq.setParameter("email", email);
        return tqq.getResultList().size() > 0;

    }

    @RolesAllowed("getAllUnconfirmedBusinessWorkers")
    public List<AccessLevel> getUnconfirmedBusinessWorkers() {
        TypedQuery<AccessLevel> tqq = em.createNamedQuery("BusinessWorker.findALlUnconfirmed", AccessLevel.class);
        return tqq.getResultList();

    }

    @PermitAll
    @Override
    public void edit(Account entity) throws FacadeException {
        try {
            super.edit(entity);
        } catch (ConstraintViolationException e) {
            switch (e.getConstraintName()) {
                case LOGIN_CONSTRAINT:
                    throw AccountFacadeException.loginReserved(e);
                case EMAIL_CONSTRAINT:
                    throw AccountFacadeException.emailReserved(e);
            }
        }
    }

    @PermitAll
    @Override
    public void create(Account entity) throws FacadeException {
        try {
            super.create(entity);
        } catch (ConstraintViolationException e) {
            switch (e.getConstraintName()) {
                case LOGIN_CONSTRAINT:
                    throw AccountFacadeException.loginReserved(e);
                case EMAIL_CONSTRAINT:
                    throw AccountFacadeException.emailReserved(e);
            }
        }
    }


    @PermitAll
    @Override
    public void remove(Account entity) throws FacadeException {
        super.remove(entity);
    }

    @PermitAll
    @Override
    public Account find(Object id) throws FacadeException {
        return super.find(id);
    }

    @PermitAll
    @Override
    public List findRange(int[] range) throws FacadeException {
        return super.findRange(range);
    }

    @PermitAll
    @Override
    public long count() throws FacadeException {
        return super.count();
    }
}
