package com.cruisely.exceptions;

/**
 * Class representing exceptions thrown for ReservationManager class
 */
public class ReservationManagerException extends BaseAppException {
    public ReservationManagerException(String message) {
        super(message);
    }

    public ReservationManagerException(String message, Throwable cause) {
        super(message, cause);
    }


}
