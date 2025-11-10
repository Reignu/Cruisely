package com.cruisely.exceptions.status_mappers;

import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.TransactionRepeaterException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.*;
import static com.cruisely.common.I18n.*;

@Provider
public class BaseAppExceptionMapper implements ExceptionMapper<BaseAppException> {
    @Override
    public Response toResponse(BaseAppException exception) {
        if (exception instanceof TransactionRepeaterException) {
            return Response.status(INTERNAL_SERVER_ERROR).entity(APP_INTERNAL_SERVER_ERROR).build();
        }

        Status statusCode;
        switch (exception.getMessage()) {
            case NO_SUCH_ELEMENT_ERROR:
                statusCode = NOT_FOUND;
                break;
            case OPTIMISTIC_LOCK_EXCEPTION:
                statusCode = CONFLICT;
                break;
            default:
                statusCode = BAD_REQUEST;
        }
        return Response.status(statusCode).entity(exception.getMessage()).build();
    }
}
