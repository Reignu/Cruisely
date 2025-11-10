package com.cruisely.cruise.managers;

import lombok.extern.java.Log;
import com.cruisely.entities.common.AlterType;
import com.cruisely.entities.common.BaseEntity;
import com.cruisely.entities.common.wrappers.AlterTypeWrapper;
import com.cruisely.entities.auth.Account;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.cruise.facades.AccountFacadeMow;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@Log
@Stateful
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors(TrackingInterceptor.class)
public class BaseManagerMow {
    @Context
    private SecurityContext context;

    @Inject
    private AccountFacadeMow accountFacade;

    protected void setUpdatedMetadata(BaseEntity... entities) throws BaseAppException {
        AlterTypeWrapper update = accountFacade.getAlterTypeWrapperByAlterType(AlterType.UPDATE);
        for (BaseEntity e : entities) {
            e.setAlterType(update);
            e.setAlteredBy(getCurrentUser());
        }
    }

    protected void setCreatedMetadata(Account creator, BaseEntity... entities) {
        AlterTypeWrapper insert = accountFacade.getAlterTypeWrapperByAlterType(AlterType.INSERT);
        for (BaseEntity e : entities) {
            e.setAlterType(insert);
            e.setAlteredBy(creator);
            e.setCreatedBy(creator);
        }
    }

    @RolesAllowed("authenticatedUser")
    public Account getCurrentUser() throws BaseAppException {
        return accountFacade.findByLogin(context.getUserPrincipal().getName());
    }
}
