package com.capbase.capbase.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // De momento la dejamos fija para avanzar.
    private static final String SECRET_KEY = "capbaseclaveprivadasegura123456789capbase";

    // 24 horas
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public String generarToken(String email) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(email)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
}