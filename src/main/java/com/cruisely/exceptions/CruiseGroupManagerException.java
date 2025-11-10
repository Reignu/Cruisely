package com.cruisely.exceptions;

/**
 * Class representing exception thrown by CruiseGroupManager class
 */
public class CruiseGroupManagerException extends BaseAppException{
    public CruiseGroupManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CruiseGroupManagerException(String message) {
        super(message);
    }
}
