package com.example.taskmanager.service;

import com.example.taskmanager.dto.LoginResponseDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final String secretKeyString;
    private final long jwtExpirationMs;
    public JwtService(
            @Value("${jwt.secret}") String secretKeyString,
            @Value("${jwt.expiration}") long jwtExpirationMs
    ) {
        this.secretKeyString = secretKeyString;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    private SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKeyString);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public LoginResponseDTO issueToken(String email){
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKeyString);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        Instant now = Instant.now();
        Instant expiryInstant = now.plusMillis(jwtExpirationMs);


        // 2. Assemble and sign the secure compact payload string
        String token= Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryInstant))
                .signWith(key)
                .compact();
        return new LoginResponseDTO(token,jwtExpirationMs,now);
    }


    public boolean validateToken(String token){
        try{
            extractAllClaims(token);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    public String extractSubject(String token){
        return extractAllClaims(token).getSubject();

    }
    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
