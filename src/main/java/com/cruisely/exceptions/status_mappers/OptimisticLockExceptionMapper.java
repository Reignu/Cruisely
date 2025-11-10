package com.cruisely.exceptions.status_mappers;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static com.cruisely.common.I18n.OPTIMISTIC_LOCK_EXCEPTION;

@Provider
public class OptimisticLockExceptionMapper implements ExceptionMapper<OptimisticLockException> {
    @Override
    public Response toResponse(OptimisticLockException exception) {
        return Response.status(CONFLICT).entity(OPTIMISTIC_LOCK_EXCEPTION).build();
    }
}
