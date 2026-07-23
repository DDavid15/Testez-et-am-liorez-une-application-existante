package com.openclassrooms.etudiant.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String LOGIN = "LOGIN";
    private static final long EXPIRATION = 3_600_000L;

    private JwtService jwtService;
    private String secret;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // La clé de test contient 32 octets, soit 256 bits.
        String testKey = "01234567890123456789012345678901";

        secret = Base64.getEncoder()
                .encodeToString(
                        testKey.getBytes(StandardCharsets.UTF_8)
                );

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                secret
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                EXPIRATION
        );
    }

    @Test
    void generateTokenShouldContainExpectedUserInformation() {
        // GIVEN : un utilisateur authentifié
        UserDetails userDetails = User.builder()
                .username(LOGIN)
                .password("PASSWORD")
                .authorities(Collections.emptyList())
                .build();

        // WHEN : le service génère un token JWT
        String token = jwtService.generateToken(userDetails);

        // THEN : le token contient le login et une expiration valide
        assertNotNull(token);
        assertFalse(token.isBlank());

        SecretKey signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(LOGIN, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());

        assertTrue(
                claims.getExpiration().after(claims.getIssuedAt())
        );

        assertEquals(
                EXPIRATION,
                claims.getExpiration().getTime()
                        - claims.getIssuedAt().getTime()
        );
    }
}