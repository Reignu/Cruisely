package com.cruisely.exceptions;

/**
 * Class representing exceptions thrown by endpoint class
 */
public class EndpointException extends BaseAppException {
    public EndpointException(String message, Throwable cause) {
        super(message, cause);
    }

    public EndpointException(String message) {
        super(message);
    }
}
