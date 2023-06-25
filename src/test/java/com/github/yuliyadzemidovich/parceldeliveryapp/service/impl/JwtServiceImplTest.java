package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;

import static com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil.TEST_USER_1_EMAIL;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private final JwtServiceImpl jwtService = new JwtServiceImpl();

    @Test
    void generateToken() throws JOSEException, ParseException {
        byte[] testSharedSecret = generateSharedSecret();

        // mock property injection
        jwtService.sharedSecret = testSharedSecret;

        // method under test
        String jwt = jwtService.generateToken(TEST_USER_1_EMAIL);

        // On the consumer side, parse the JWS and verify its HMAC
        SignedJWT signedJWT = SignedJWT.parse(jwt);
        JWSVerifier verifier = new MACVerifier(testSharedSecret);
        assertTrue(signedJWT.verify(verifier));

        // Assert the JWT claims
        JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
        assertEquals(TEST_USER_1_EMAIL, jwtClaimsSet.getSubject());
        assertEquals(JwtServiceImpl.JWT_ISSUER, jwtClaimsSet.getIssuer());
        assertTrue(new Date().before(jwtClaimsSet.getExpirationTime()));
    }

    @Test
    void isValidToken() {
        // mock sharedSecret property injection
        jwtService.sharedSecret = generateSharedSecret();
        String jwt = jwtService.generateToken(TEST_USER_1_EMAIL);

        // method under test
        boolean isValidToken = jwtService.isValidToken(jwt);
        assertTrue(isValidToken);
    }

    private static byte[] generateSharedSecret() {
        // Generate random 512-bit (64-byte) shared secret
        SecureRandom random = new SecureRandom();
        byte[] testSharedSecret = new byte[64];
        random.nextBytes(testSharedSecret);
        return testSharedSecret;
    }
}
