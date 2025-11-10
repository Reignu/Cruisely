package com.cruisely.exceptions.status_mappers;

import javax.ejb.AccessLocalException;
import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static com.cruisely.common.I18n.APP_FORBIDDEN;
import static com.cruisely.common.I18n.APP_INTERNAL_SERVER_ERROR;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {
    @Override
    public Response toResponse(EJBException exception) {
        if (exception.getCausedByException() instanceof AccessLocalException) {
            return Response.status(FORBIDDEN).entity(APP_FORBIDDEN).build();
        }
        return Response.status(INTERNAL_SERVER_ERROR).entity(APP_INTERNAL_SERVER_ERROR).build();
    }
}
