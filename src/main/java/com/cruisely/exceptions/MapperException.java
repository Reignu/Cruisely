package com.cruisely.exceptions;

/**
 * Class representing exceptions thrown for mapping
 */
public class MapperException extends BaseAppException{
    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(String message) {
        super(message);
    }
}
