package com.cruisely.auth.endpoints;

import com.cruisely.common.endpoints.TransactionalEndpoint;
import com.cruisely.exceptions.BaseAppException;
import com.cruisely.auth.dto.AuthenticateDto;
import com.cruisely.utils.TransactionRepeater;

import java.time.LocalDateTime;

/**
 * Interface used for user login/authentication operations.
 */
public interface AuthenticateEndpointLocal extends TransactionalEndpoint {

  /**
   * Updates database fields in case of an unsuccessful login attempt.
   *
   * @param login  User login
   * @param IpAddr User IP address
   * @param time   Timestamp
   */
    void updateIncorrectAuthenticateInfo(String login, String IpAddr, LocalDateTime time) throws BaseAppException;

  /**
   * Updates database fields in case of a successful login.
   *
   * @param login  User login
   * @param IpAddr User IP address
   * @param time   Timestamp
   * @return JWT token
   */
    String updateCorrectAuthenticateInfo(String login, String IpAddr, LocalDateTime time) throws BaseAppException;


  /**
   * Verifies two-step (code) authentication and updates database fields on successful authentication.
   *
   * @param login  User login
   * @param code   Two-factor authentication code
   * @param IpAddr User IP address
   * @param time   Timestamp
   * @return JWT token
   * @throws BaseAppException Base application exception
   */

    String authWCodeUpdateCorrectAuthenticateInfo(String login, String code, String IpAddr, LocalDateTime time) throws BaseAppException;

  /**
   * Sends an email used for two-factor authentication.
   *
   * @param auth AuthenticateDto containing authentication data
   * @throws BaseAppException Base application exception
   */
   void sendAuthenticationCodeEmail(AuthenticateDto auth) throws BaseAppException;

  /**
   * Refreshes a JWT token.
   *
   * @param token Current JWT token
   * @return Refreshed JWT token
   * @throws BaseAppException Base application exception thrown if the provided token is invalid or its subject is not authorized to refresh
   */
    String refreshToken(String token) throws BaseAppException;
}
