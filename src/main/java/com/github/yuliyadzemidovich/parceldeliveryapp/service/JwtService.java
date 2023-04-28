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
}
