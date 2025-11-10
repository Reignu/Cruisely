package com.cruisely.common.facades;

import org.hibernate.exception.ConstraintViolationException;
import com.cruisely.exceptions.FacadeException;

import javax.annotation.security.DenyAll;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@DenyAll
public abstract class AbstractFacade<T> {

    private final Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    protected void create(T entity) throws FacadeException {
        try {
            getEntityManager().persist(entity);
            getEntityManager().flush();
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw (ConstraintViolationException) e.getCause();
            }
            throw FacadeException.databaseOperation();
        }
    }


    protected void edit(T entity) throws FacadeException {
        try {
            getEntityManager().merge(entity);
            getEntityManager().flush();
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw (ConstraintViolationException) e.getCause();
            }
            throw FacadeException.databaseOperation();
        }
    }

    protected void remove(T entity) throws FacadeException {
        try {
            getEntityManager().remove(getEntityManager().merge(entity));
            getEntityManager().flush();
        } catch (PersistenceException exp) {
            throw FacadeException.databaseOperation();
        } catch (IllegalArgumentException exp) {
            throw FacadeException.noSuchElement();
        }
    }

    protected T find(Object id) throws FacadeException {
        try {
            return getEntityManager().find(entityClass, id);
        } catch (PersistenceException exp) {
            throw FacadeException.databaseOperation();
        }
    }

    protected List findAll() throws FacadeException {
        try {
            CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            return getEntityManager().createQuery(cq).getResultList();
        } catch (PersistenceException exp) {
            throw FacadeException.databaseOperation();
        }
    }

    protected List findRange(int[] range) throws FacadeException {
        try {
            CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            Query q = getEntityManager().createQuery(cq);
            q.setMaxResults(range[1] - range[0] + 1);
            q.setFirstResult(range[0]);
            return q.getResultList();
        } catch (PersistenceException exp) {
            throw FacadeException.databaseOperation();
        }
    }

    protected long count() throws FacadeException {
        try {
            CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
            Root<T> rt = cq.from(entityClass);
            cq.select(getEntityManager().getCriteriaBuilder().count(rt));
            Query q = getEntityManager().createQuery(cq);
            return ((long) q.getSingleResult());
        } catch (PersistenceException exp) {
            throw FacadeException.databaseOperation();
        }

    }

}
