package com.example.taskmanager.service;

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
    public String issueToken(String email){
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKeyString);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        Instant now = Instant.now();
        Instant expiryInstant = now.plusMillis(jwtExpirationMs);

        // 2. Assemble and sign the secure compact payload string
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryInstant))
                .signWith(key)
                .compact();
    }

}
