package com.cruisely.security;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;


/**
 * Defines the identity store used for authentication via the Soteria API
 */
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "java:app/jdbc/ssbd03glassfishDS",
        callerQuery = "SELECT password_hash as password FROM glassfish_auth_view WHERE login = ?",
        groupsQuery = "SELECT access_level as group_name FROM glassfish_auth_view WHERE login = ?",
        hashAlgorithm = SHA256Hash.class
)
@ApplicationScoped
public class IdentityStore {
}