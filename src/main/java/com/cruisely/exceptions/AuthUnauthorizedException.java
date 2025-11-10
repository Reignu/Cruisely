package com.cruisely.exceptions;

/**
 * Class responsible for exception during login process
 */
public class AuthUnauthorizedException extends Exception {

    public AuthUnauthorizedException(String message) {
        super(message);
    }
}