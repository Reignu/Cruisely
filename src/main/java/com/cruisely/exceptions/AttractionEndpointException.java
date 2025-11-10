package com.cruisely.exceptions;

public class AttractionEndpointException extends BaseAppException{
    public AttractionEndpointException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttractionEndpointException(String message) {
        super(message);
    }
}
