package com.cruisely.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.cruisely.exceptions.ETagException;
import com.cruisely.utils.PropertiesReader;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.interceptor.Interceptors;
import java.text.ParseException;
import java.util.Properties;

import static com.cruisely.common.I18n.ETAG_CREATION_ERROR;

/**
 * Class providing static methods for handling ETag operations
 */
@Interceptors(TrackingInterceptor.class)
public class EntityIdentitySignerVerifier {
    private static final Properties securityProperties = PropertiesReader.getSecurityProperties();

    /**
     * Method for creating an ETag
     *
     * @param entity Entity for which the ETag will be created
     * @return Serialized ETag value if creation succeeds
     */
    public static String calculateEntitySignature(SignableEntity entity) throws ETagException {
        try {
            JWSSigner signer = new MACSigner(securityProperties.getProperty("etag.secret"));

            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(entity.getSignablePayload()));
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new ETagException(ETAG_CREATION_ERROR, e);
        }
    }

    /**
     * Method that validates the ETag signature
     *
     * @param tag ETag value
     * @return true if the ETag signature is valid, false otherwise
     */
    public static boolean validateEntitySignature(String tag) {
        try {
            JWSObject jwsObject = JWSObject.parse(tag);
            JWSVerifier verifier = new MACVerifier(securityProperties.getProperty("etag.secret"));
            return jwsObject.verify(verifier);

        } catch (ParseException | JOSEException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifies that the given entity matches the provided ETag
     *
     * @param entity Entity to verify
     * @param tag    ETag value
     * @return true if the entity and ETag match and the signature is valid
     */
    public static boolean verifyEntityIntegrity(SignableEntity entity, String tag) {
        try {
            final String header = JWSObject.parse(tag).getPayload().toString();
            final String signableEntityPayload = entity.getSignablePayload();
            return validateEntitySignature(tag) && signableEntityPayload.equals(header);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}
