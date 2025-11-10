package com.cruisely.exceptions;

import javax.ejb.ApplicationException;

/**
 * Abstract class representing base app exception
 * After its occurence, database transactions are cancelled
 */
@ApplicationException(rollback = true)
abstract public class BaseAppException extends Exception {

    protected BaseAppException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BaseAppException(String message) {
        super(message);
    }

}
