package com.example.taskmanager.service;



import com.example.taskmanager.dto.LoginResponseDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;




import javax.crypto.SecretKey;



import java.time.Instant;
import java.util.Date;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class JwtServiceTest {

    private JwtService jwtService;

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

    @Test
    void shouldPassSignatureCheck_whenTokenIsAuthentic(){
        String email="example@gmail.com";
        LoginResponseDTO loginResponseDTO =jwtService.issueToken(email);

        boolean isSignatureIntact=jwtService.validateToken(loginResponseDTO.token());

        assertTrue(isSignatureIntact);

    }
    @Test
    void validateToken_shouldReturnFalse_whenTokenIsTamperedWith() {
        String email = "example@gmail.com";
        LoginResponseDTO loginResponseDTO = jwtService.issueToken(email);

        String token = loginResponseDTO.token();

        String tamperedToken = token.substring(0, token.length() - 1) + "x";

        boolean result = jwtService.validateToken(tamperedToken);

        assertThat(result).isFalse();
    }

    @Test
    void shouldFailSignatureSheck_whenTokenForamtIsNotValid(){
        String tamperedWithToken="not a valid token";
        boolean isSignatureIntact=jwtService.validateToken(tamperedWithToken);

        assertFalse(isSignatureIntact);
    }
    @Test
    void shouldExtractCorrectSubjectFromToken_whenTokenIsValid(){
        String expectedEmail = "mukundi@gmail.com";
        LoginResponseDTO loginResponseDTO=jwtService.issueToken(expectedEmail);
        String extractedEmail=jwtService.extractSubject(loginResponseDTO.token());

        assertThat(extractedEmail).isEqualTo(expectedEmail);

    }

    @Test
    void shouldExtractExpiration_whenTokenIsValid(){
        String email = "mukundi@gmail.com";

        LoginResponseDTO loginResponseDTO = jwtService.issueToken(email);

        Date expiration = jwtService.extractExpiration(loginResponseDTO.token());
        assertThat(expiration).isAfter(new Date());
    }
    @Test
    void isTokenExpired_shouldReturnFalse_whenTokenIsNotExpired() {
        String email = "mukundi@gmail.com";

        LoginResponseDTO loginResponseDTO = jwtService.issueToken(email);

        boolean result = jwtService.isTokenExpired(loginResponseDTO.token());

        assertThat(result).isFalse();
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsExpired() {
        JwtService expiredJwtService = new JwtService(testSecret, -1000L);

        LoginResponseDTO loginResponseDTO =
                expiredJwtService.issueToken("example@gmail.com");

        boolean result = jwtService.validateToken(loginResponseDTO.token());

        assertThat(result).isFalse();
    }


    @Test
    void validateToken_shouldReturnFalse_whenTokenWasSignedWithDifferentSecret(){
        JwtService otherService=new JwtService( "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY",testExpirationMs);

        LoginResponseDTO loginResponseDTO=otherService.issueToken("example@gmail.com");
        boolean isValid=jwtService.validateToken(loginResponseDTO.token());
        assertFalse(isValid);
        System.out.println(loginResponseDTO.token());
    }




}
