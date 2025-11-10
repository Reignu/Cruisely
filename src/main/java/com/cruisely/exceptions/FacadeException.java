package com.cruisely.exceptions;

import static com.cruisely.common.I18n.*;


/**
 * Class representing exceptions thrown for facade class
 */
public class FacadeException extends BaseAppException {

    public FacadeException(String message) {
        super(message);
    }

    public FacadeException(String message, Throwable cause) {
        super(message, cause);
    }

    public static FacadeException noSuchElement() throws FacadeException {
        throw new FacadeException(NO_SUCH_ELEMENT_ERROR);
    }

    public static FacadeException optimisticLock() throws FacadeException {
        throw new FacadeException(OPTIMISTIC_LOCK_EXCEPTION);
    }

    public static FacadeException databaseOperation() throws FacadeException {
        throw new FacadeException(DATABASE_OPERATION_ERROR);
    }
}
