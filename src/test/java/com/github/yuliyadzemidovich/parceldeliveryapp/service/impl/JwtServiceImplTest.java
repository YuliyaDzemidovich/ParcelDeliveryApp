package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private final JwtServiceImpl jwtService = new JwtServiceImpl();

    @Test
    void generateToken() throws JOSEException, ParseException {
        final String TEST_USER_EMAIL = "example@mail.com";

        // Generate random 512-bit (64-byte) shared secret
        SecureRandom random = new SecureRandom();
        byte[] testSharedSecret = new byte[64];
        random.nextBytes(testSharedSecret);

        // mock property injection
        jwtService.sharedSecret = testSharedSecret;

        // method under test
        String jwt = jwtService.generateToken(TEST_USER_EMAIL);

        // On the consumer side, parse the JWS and verify its HMAC
        SignedJWT signedJWT = SignedJWT.parse(jwt);
        JWSVerifier verifier = new MACVerifier(testSharedSecret);
        assertTrue(signedJWT.verify(verifier));

        // Assert the JWT claims
        JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
        assertEquals(TEST_USER_EMAIL, jwtClaimsSet.getSubject());
        assertEquals(JwtServiceImpl.JWT_ISSUER, jwtClaimsSet.getIssuer());
        assertTrue(new Date().before(jwtClaimsSet.getExpirationTime()));
    }

    @Test
    void generateTestToken() throws JOSEException, ParseException {
        // Generate random 512-bit (64-byte) shared secret
        SecureRandom random = new SecureRandom();
        byte[] sharedSecret = new byte[64];
        random.nextBytes(sharedSecret);
        System.out.println("Shared Secret:\n" + Arrays.toString(sharedSecret));

        // Create HMAC signer
        JWSSigner signer = new MACSigner(sharedSecret);

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("alice")
                .issuer("https://c2id.com")
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);

        // Apply the HMAC protection
        signedJWT.sign(signer);

        // Serialize to compact form, produces something like
        // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
        String s = signedJWT.serialize();

        // On the consumer side, parse the JWS and verify its HMAC
        signedJWT = SignedJWT.parse(s);

        JWSVerifier verifier = new MACVerifier(sharedSecret);

        assertTrue(signedJWT.verify(verifier));

        // Retrieve / verify the JWT claims according to the app requirements
        JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
        assertEquals("alice", jwtClaimsSet.getSubject());
        assertEquals("https://c2id.com", jwtClaimsSet.getIssuer());
        assertTrue(new Date().before(jwtClaimsSet.getExpirationTime()));
        System.out.println("Claims Set:\n" + jwtClaimsSet);
    }
}
