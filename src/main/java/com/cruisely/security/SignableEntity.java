package com.cruisely.security;

/**
 * Interface sharing methods returning Etag value
 */
public interface SignableEntity {
    String getSignablePayload();
}
