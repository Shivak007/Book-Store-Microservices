package com.bookstore.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    public String generateToken(UserDetails userDetails, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSigningKey())
            .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
