package com.example.taskmanager.service;



import com.example.taskmanager.dto.LoginResponseDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;



import javax.crypto.SecretKey;


import java.time.Instant;


import static org.assertj.core.api.Assertions.assertThat;



public class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;
    // Exact 256-bit test secret key string
    // Replace the Hex string with a valid Base64 string:
    private final String testSecret = "NDBFNjNSbTY1VUpYNm4ycjV1Mzh4MkZBM0Y0NDI4NDcyQktiNFBkU2dWNms5WnA";

    private final long testExpirationMs = 3600000; // 1 hour in milliseconds

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(testSecret,testExpirationMs);

    }

    @Test
    void issueToken_ShouldGenerateValidJwt_WithCorrectClaimsAndExpiration() {
        // Arrange
        String testEmail = "developer@taskmanager.com";
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");

        // Act
        LoginResponseDTO loginResponseDTO =jwtService.issueToken(testEmail);

        //Assert
        assertThat(loginResponseDTO.token()).isNotBlank();
        byte[] keyBytes = Decoders.BASE64URL.decode(testSecret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(loginResponseDTO.token())
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(testEmail);
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(Instant.now());

        long expectedExpiryTime = claims.getIssuedAt().getTime() + testExpirationMs;
        assertThat(claims.getExpiration().getTime()).isEqualTo(expectedExpiryTime);
    }



}
