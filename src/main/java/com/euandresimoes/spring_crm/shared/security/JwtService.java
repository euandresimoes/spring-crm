package com.euandresimoes.spring_crm.shared.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final String secret;
    private final String ISSUER = "spring-crm";

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String generate(String subject, String role) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(subject)
                    .withClaim("role", role)
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public DecodedJWT verifyAndDecode(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token invalid or expired");
        }
    }

    private Instant getExpirationDate() {
        return Instant.now().plusMillis(3600000); // 1 hour
    }
}
