package com.euandresimoes.spring_crm.auth.infra.security.jwt;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

    private final Algorithm algorithm;

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generate(String subject, String role) {
        int expiration = 1000 * 60 * 60;

        try {
            String token = JWT.create()
                    .withIssuer("spring-crm")
                    .withSubject(subject)
                    .withClaim("role", role)
                    .withExpiresAt(Instant.now().plusMillis(expiration))
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException e) {
            throw new JWTCreationException(e.getMessage(), null);
        }
    }

    public DecodedJWT verifyAndDecode(String token) {
        return JWT.require(algorithm)
                .withIssuer("spring-crm")
                .build()
                .verify(token);
    }
}
