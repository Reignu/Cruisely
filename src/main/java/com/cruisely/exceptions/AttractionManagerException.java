package com.cruisely.exceptions;

/**
 * Class representing exception thrown by AttractionManager class
 * 
 */
public class AttractionManagerException extends BaseAppException {
    public AttractionManagerException(String message) {
        super(message);
    }

    public AttractionManagerException(String message, Throwable cause) {
        super(message, cause);
    }


}
