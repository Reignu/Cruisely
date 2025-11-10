package com.cruisely.exceptions;

/**
 * Class representing exceptions thrown by CruiseManager class
 */
public class CruiseManagerException extends BaseAppException {
    public CruiseManagerException(String message) {
        super(message);
    }

    public CruiseManagerException(String message, Throwable cause) {
        super(message, cause);
    }


}
