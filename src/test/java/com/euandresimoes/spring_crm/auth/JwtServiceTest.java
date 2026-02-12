package com.euandresimoes.spring_crm.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.euandresimoes.spring_crm.shared.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the JWT service.
 * Validates JWT token generation, verification, and decoding.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "test-secret-key-for-jwt";

    @BeforeEach
    void setUp() {
        // Initialize the service before each test
        jwtService = new JwtService(secret);
    }

    @Test
    @DisplayName("Should generate a valid JWT token successfully")
    void shouldGenerateTokenSuccessfully() {
        // Arrange
        String subject = "123e4567-e89b-12d3-a456-426614174000";
        String role = "USER";

        // Act
        String token = jwtService.generate(subject, role);

        // Assert
        // Verify that the token was generated
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        // Verify that the token has the JWT format (3 parts separated by a dot)
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should include the correct subject in the generated token")
    void shouldIncludeCorrectSubjectInToken() {
        // Arrange
        String subject = "user-id-123";
        String role = "ADMIN";

        // Act
        String token = jwtService.generate(subject, role);

        // Decode the token to verify the content
        DecodedJWT decodedJWT = jwtService.verifyAndDecode(token);

        // Assert
        assertThat(decodedJWT.getSubject()).isEqualTo(subject);
    }

    @Test
    @DisplayName("Should include the correct role as a claim in the token")
    void shouldIncludeCorrectRoleInToken() {
        // Arrange
        String subject = "user-id-456";
        String role = "ADMIN";

        // Act
        String token = jwtService.generate(subject, role);

        // Decode the token to verify the content
        DecodedJWT decodedJWT = jwtService.verifyAndDecode(token);

        // Assert
        assertThat(decodedJWT.getClaim("role").asString()).isEqualTo(role);
    }

    @Test
    @DisplayName("Should include the 'spring-crm' issuer in the token")
    void shouldIncludeCorrectIssuerInToken() {
        // Arrange
        String subject = "user-id-789";
        String role = "USER";

        // Act
        String token = jwtService.generate(subject, role);

        // Decode the token to verify the content
        DecodedJWT decodedJWT = jwtService.verifyAndDecode(token);

        // Assert
        assertThat(decodedJWT.getIssuer()).isEqualTo("spring-crm");
    }

    @Test
    @DisplayName("Should include an expiration date in the token")
    void shouldIncludeExpirationDateInToken() {
        // Arrange
        String subject = "user-id-999";
        String role = "USER";

        Instant antes = Instant.now();

        // Act
        String token = jwtService.generate(subject, role);

        Instant depois = Instant.now();

        // Decode the token to verify the content
        DecodedJWT decodedJWT = jwtService.verifyAndDecode(token);

        // Assert
        assertThat(decodedJWT.getExpiresAt()).isNotNull();

        // Verify that the expiration is approximately 1 hour in the future
        // (1000ms * 60s * 60min = 3600000ms = 1 hour)
        Instant expiracao = decodedJWT.getExpiresAt().toInstant();
        long diferencaEmMs = expiracao.toEpochMilli() - antes.toEpochMilli();

        // The difference should be approximately 1 hour (with a margin of a few
        // seconds)
        assertThat(diferencaEmMs).isGreaterThan(3590000); // 59min 50s
        assertThat(diferencaEmMs).isLessThan(3610000); // 60min 10s
    }

    @Test
    @DisplayName("Should verify and decode a valid token successfully")
    void shouldVerifyAndDecodeValidToken() {
        // Arrange
        String subject = "user-id-abc";
        String role = "USER";

        // Generate a valid token
        String token = jwtService.generate(subject, role);

        // Act
        DecodedJWT decodedJWT = jwtService.verifyAndDecode(token);

        // Assert
        assertThat(decodedJWT).isNotNull();
        assertThat(decodedJWT.getSubject()).isEqualTo(subject);
        assertThat(decodedJWT.getClaim("role").asString()).isEqualTo(role);
        assertThat(decodedJWT.getIssuer()).isEqualTo("spring-crm");
    }

    @Test
    @DisplayName("Should throw an exception when verifying a token with an invalid signature")
    void shouldThrowExceptionWhenVerifyingTokenWithInvalidSignature() {
        // Arrange
        // Create a token with a different secret
        Algorithm algoritmoErrado = Algorithm.HMAC256("secret-errado");
        String tokenInvalido = JWT.create()
                .withIssuer("spring-crm")
                .withSubject("user-id")
                .withClaim("role", "USER")
                .withExpiresAt(Instant.now().plusMillis(3600000))
                .sign(algoritmoErrado);

        // Act & Assert
        assertThatThrownBy(() -> jwtService.verifyAndDecode(tokenInvalido))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("Should throw an exception when verifying a token with an invalid issuer")
    void shouldThrowExceptionWhenVerifyingTokenWithInvalidIssuer() {
        // Arrange
        // Create a token with a different issuer
        Algorithm algoritmo = Algorithm.HMAC256(secret);
        String tokenInvalido = JWT.create()
                .withIssuer("issuer-errado")
                .withSubject("user-id")
                .withClaim("role", "USER")
                .withExpiresAt(Instant.now().plusMillis(3600000))
                .sign(algoritmo);

        // Act & Assert
        assertThatThrownBy(() -> jwtService.verifyAndDecode(tokenInvalido))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("Should generate different tokens for different subjects")
    void shouldGenerateDifferentTokensForDifferentSubjects() {
        // Arrange
        String subject1 = "user-1";
        String subject2 = "user-2";
        String role = "USER";

        // Act
        String token1 = jwtService.generate(subject1, role);
        String token2 = jwtService.generate(subject2, role);

        // Assert
        // The tokens must be different
        assertThat(token1).isNotEqualTo(token2);

        // But both must be valid
        DecodedJWT decoded1 = jwtService.verifyAndDecode(token1);
        DecodedJWT decoded2 = jwtService.verifyAndDecode(token2);

        assertThat(decoded1.getSubject()).isEqualTo(subject1);
        assertThat(decoded2.getSubject()).isEqualTo(subject2);
    }

    @Test
    @DisplayName("Should generate different tokens for different roles")
    void shouldGenerateDifferentTokensForDifferentRoles() {
        // Arrange
        String subject = "user-id";
        String role1 = "USER";
        String role2 = "ADMIN";

        // Act
        String token1 = jwtService.generate(subject, role1);
        String token2 = jwtService.generate(subject, role2);

        // Assert
        // The tokens must be different
        assertThat(token1).isNotEqualTo(token2);

        // But both must be valid
        DecodedJWT decoded1 = jwtService.verifyAndDecode(token1);
        DecodedJWT decoded2 = jwtService.verifyAndDecode(token2);

        assertThat(decoded1.getClaim("role").asString()).isEqualTo(role1);
        assertThat(decoded2.getClaim("role").asString()).isEqualTo(role2);
    }
}
