package com.cruisely.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.exceptions.JWTException;
import com.cruisely.utils.PropertiesReader;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.interceptor.Interceptors;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.cruisely.common.I18n.TOKEN_DECODE_ERROR;
import static com.cruisely.common.I18n.TOKEN_INVALIDATE_ERROR;

/**
 * Class responsible for managing JWT tokens.
 */
@Interceptors(TrackingInterceptor.class)
public class JWTHandler {
    private static final Properties securityProperties = PropertiesReader.getSecurityProperties();

    /**
     * Creates a new JWT token
     *
     * @param claims  Map of claim key-value pairs representing token claims
     * @param subject String specifying the token subject
     * @return Generated token as a String
     */
    public static String createToken(Map<String, ?> claims, String subject) {
        Algorithm algorithm = Algorithm.HMAC256(getJWTSecret());
        return JWT.create()
                .withPayload(claims).withSubject(subject)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plus(getDefaultValidityInMinutes(), ChronoUnit.MINUTES)))
                .withJWTId(UUID.randomUUID().toString())
                .withIssuer(getJWTIssuer())
                .sign(algorithm);
    }

    /**
     * Creates a new email token
     *
     * @param claims Map of claim key-value pairs representing token claims
     * @param subject String specifying the token subject
     * @return Generated token as a String
     */
    public static String createTokenEmail(Map<String, ?> claims, String subject) {
        Algorithm algorithm = Algorithm.HMAC256(getJWTSecret());
        return JWT.create()
                .withPayload(claims).withSubject(subject)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plus(getDefaultValidityInHours(), ChronoUnit.HOURS)))
                .withJWTId(UUID.randomUUID().toString())
                .withIssuer(getJWTIssuer())
                .sign(algorithm);
    }

    /**
     * Refreshes an existing token and returns a new one.
     * If the provided token has expired, a TokenExpiredException is thrown.
     *
     * @param token Existing token
     * @return New JWT token preserving the original payload
     * @throws TokenExpiredException when token is expired
     */
    public static String refreshToken(String token) throws BaseAppException {
        validateToken(token);
        DecodedJWT decodedToken = JWT.decode(token);
        if (decodedToken.getExpiresAt().before(new Date())) {
            throw JWTException.tokenExpired();
        }

        //getClaims returns Map<String, Claim> where Claim needs to be converted to Object type,
        //to satisfy allowed Claim types, thus mapping it explicitly
        Map<String, Object> convertedClaims = decodedToken.getClaims().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().as(Object.class)));
        return createToken(convertedClaims, decodedToken.getSubject());
    }


    /**
     * Returns the token claims as a Map<String, Claim>
     *
     * @param token Token to decode
     * @return Map of claims <String, Claim>
     * @throws JWTException decoding error
     */
    public static Map<String, Claim> getClaimsFromToken(String token) throws JWTException {
        try {
            return JWT.decode(token).getClaims();
        } catch (JWTDecodeException e) {
            throw new JWTException(TOKEN_DECODE_ERROR);
        }
    }

    /**
     * Returns the issuer from the token
     *
     * @param token Token to decode
     * @return issuer
     * @throws JWTDecodeException decoding error
     */
    public static String getIssuerFromToken(String token) throws JWTException {
        try {
            return JWT.decode(token).getIssuer();
        } catch (JWTDecodeException e) {
            throw new JWTException(TOKEN_DECODE_ERROR);
        }
    }

    /**
     * Returns the token expiration time
     *
     * @param token Token to decode
     * @return expiration Date
     * @throws JWTDecodeException decoding error
     */
    public static Date getExpirationTimeFromToken(String token) throws JWTException {
        try {
            return JWT.decode(token).getExpiresAt();
        } catch (JWTDecodeException e) {
            throw new JWTException(TOKEN_DECODE_ERROR);
        }
    }

    /**
     * Validates an existing token using the configured algorithm and secret.
     * If validation fails, a JWTVerificationException is thrown.
     *
     * @param token Token to validate
     * @throws JWTVerificationException validation error
     */
    public static void validateToken(String token) throws JWTException {
        Algorithm algorithm = Algorithm.HMAC256(getJWTSecret());
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new JWTException(TOKEN_INVALIDATE_ERROR);
        }
    }

    private static long getDefaultValidityInMinutes() {
        try {
            return Long.parseLong(securityProperties.getProperty("jwt.validityInMinutes"));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("jwt.validityInMinutes value: {" + securityProperties.getProperty("jwt.validityInMinutes") + "} could not be parsed to long, verify properties data");
        }
    }

    private static long getDefaultValidityInHours() {
        try {
            return Long.parseLong(securityProperties.getProperty("jwt.validityInHours"));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("jwt.validityInHours value: {" + securityProperties.getProperty("jwt.validityInHours") + "} could not be parsed to long, verify properties data");
        }
    }

    private static String getJWTSecret() {
        return securityProperties.getProperty("jwt.secret");
    }

    private static String getJWTIssuer() {
        return securityProperties.getProperty("jwt.issuer");
    }
}
