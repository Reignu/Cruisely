package com.cruisely.security;

import org.apache.commons.codec.digest.DigestUtils;

import javax.security.enterprise.identitystore.PasswordHash;
import java.util.Map;

/**
 * Class responsible for hashing passwords
 */
public class SHA256Hash implements PasswordHash {

    @Override
    public void initialize(Map<String, String> parameters) {
    }

    /**
     * Generates a hash for the given plaintext password
     * @param password Plaintext password
     * @return Password hash
     */
    @Override
    public String generate(char[] password) {
        return DigestUtils.sha256Hex(new String(password));
    }

    /**
     * Verifies a generated password hash
     * @param password Plaintext password
     * @param hashedPassword Stored password hash
     * @return true if verification succeeded, false otherwise
     */
    @Override
    public boolean verify(char[] password, String hashedPassword) {
        return hashedPassword.equals(generate(password));
    }
}
