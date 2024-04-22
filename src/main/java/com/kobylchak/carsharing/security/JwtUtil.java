package com.kobylchak.carsharing.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final long TO_MILLIS = 60000;
    @Value("${jwt.expiration}")
    private long expirationMinutes;
    private final Key secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secretStr) {
        this.secretKey = Keys.hmacShaKeyFor(secretStr.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userEmail) {
        return Jwts.builder()
                       .setSubject(userEmail)
                       .setIssuedAt(new Date(System.currentTimeMillis()))
                       .setExpiration(new Date(getExpiration()))
                       .signWith(secretKey)
                       .compact();

    }

    public boolean isValidToken(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                                        .setSigningKey(secretKey)
                                        .build()
                                        .parseClaimsJws(token);
        return !claimsJws.getBody().getExpiration().before(new Date());
    }

    public String getUserName(String token) {
        return Jwts.parserBuilder()
                       .setSigningKey(secretKey)
                       .build()
                       .parseClaimsJws(token)
                       .getBody()
                       .getSubject();
    }

    private long getExpiration() {
        return System.currentTimeMillis() + expirationMinutes * TO_MILLIS;
    }
}
