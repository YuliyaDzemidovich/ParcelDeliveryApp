package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.exception.ValidationException;
import com.github.yuliyadzemidovich.parceldeliveryapp.exception.WebException;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.shared.secret}")
    protected byte[] sharedSecret;

    @Override
    public String generateToken(String subject) {
        // Create HMAC signer
        JWSSigner signer;
        try {
            signer = new MACSigner(sharedSecret);
        } catch (KeyLengthException e) {
            log.error("Cannot create jwt signer", e);
            throw new WebException("Cannot create jwt", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(JWT_ISSUER)
                .expirationTime(new Date(new Date().getTime() + 30 * 60 * 1000)) // 30 min
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);

        // Apply the HMAC protection
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            log.error("Cannot sign jwt", e);
            throw new WebException("Cannot create jwt", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Serialize to compact form, produces something like
        // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
        return signedJWT.serialize();
    }

    @Override
    public boolean isValidToken(String jwt) {
        SignedJWT signedJWT;
        try {
            // verify token signature
            signedJWT = SignedJWT.parse(jwt);
            JWSVerifier verifier = new MACVerifier(sharedSecret);
            if (!signedJWT.verify(verifier)) {
                return false;
            }

            // Verify the JWT claims
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            if (!StringUtils.hasText(jwtClaimsSet.getSubject())) {
                return false;
            }
            if (!JWT_ISSUER.equals(jwtClaimsSet.getIssuer())) {
                return false;
            }
            if (jwtClaimsSet.getExpirationTime().before(new Date())) {
                return false;
            }
        } catch (ParseException | JOSEException e) {
            log.error("Exception parsing JWT", e);
            return false;
        }
        return true;
    }

    @Override
    public String extractUserEmail(String jwt) {
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(jwt);
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            return jwtClaimsSet.getSubject();
        } catch (ParseException e) {
            log.error("Exception parsing JWT", e);
            throw new ValidationException(e.getMessage());
        }
    }
}
