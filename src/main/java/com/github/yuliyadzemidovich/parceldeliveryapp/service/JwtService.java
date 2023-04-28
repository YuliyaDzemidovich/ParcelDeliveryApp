package com.github.yuliyadzemidovich.parceldeliveryapp.service;

/**
 * Service for JWT-related methods.
 */
public interface JwtService {

    String JWT_ISSUER = "parceldeliveryapp";

    /**
     * Generates JWT (JSON Web Token) with claims for user
     * @param subject who will user this token - in current implementation it is user's email
     * @return generated JWT
     */
    String generateToken(String subject);

    /**
     * Verifies that token is valid
     * @param jwt JSON Web Token
     * @return true is token is valid, false otherwise
     */
    boolean isValidToken(String jwt);

    /**
     * Extract user email from JWT claims.
     * @param jwt JSON Web Token
     * @return String with user's email
     */
    String extractUserEmail(String jwt);
}
