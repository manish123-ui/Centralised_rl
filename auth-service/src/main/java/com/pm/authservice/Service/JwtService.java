package com.pm.authservice.Service;


import com.pm.authservice.entity.User;
import com.pm.authservice.repositories.UserRepositry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;
    @Autowired
    private UserRepositry userRepositry;
    private SecretKey getSecretKey() {
        try {
            System.out.println("-> getSecretKey() started");

            if (jwtSecretKey == null) {
                System.out.println("-> ERROR: jwtSecretKey is NULL!");
                throw new IllegalArgumentException("Secret key is null");
            }

            System.out.println("-> key string length: " + jwtSecretKey.length());

            // Let's get the bytes
            byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
            System.out.println("-> Successfully got bytes. Byte length: " + keyBytes.length);

            System.out.println("-> Attempting Keys.hmacShaKeyFor...");
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);
            System.out.println("-> Keys.hmacShaKeyFor SUCCESSFUL!");

            return key;
        } catch (Throwable t) {
            System.out.println("!!! CRITICAL ERROR INSIDE getSecretKey !!!");
            System.out.println("Exception Type: " + t.getClass().getName());
            System.out.println("Exception Message: " + t.getMessage());
            t.printStackTrace(System.out); // Forces it to print to standard out logs
            throw new RuntimeException(t);
        }
    }


    public String generateAccessToken(User user) {
        try {
            System.out.println("1");
            System.out.println("id"+user.getId());
            System.out.println("key = " + jwtSecretKey);
            System.out.println("2");

            SecretKey secretKey = getSecretKey();

            System.out.println("3");

            String token = Jwts.builder()
                    .subject(user.getId().toString())
                    .claim("email", user.getEmail())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 10*60 * 10))
                    .signWith(secretKey)
                    .compact();

            System.out.println("4");

            return token;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L *60*60*24*30*6))
                .signWith(getSecretKey())
                .compact();
    }
    public String getNamefromToken(String Token) {
        try {
            // 1. Defend against null or empty strings
            if (Token == null || Token.trim().isEmpty()) {
                System.out.println("-> getNamefromToken failed: Token string is null or empty.");
                return null;
            }

            // 2. Clean up Bearer prefix if present
            if (Token.startsWith("Bearer ")) {
                Token = Token.substring(7).trim();
            }

            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(Token)
                    .getPayload();

            Long id = Long.valueOf(claims.getSubject());

            Optional<User> newuser = userRepositry.findById(id);
            if (newuser.isPresent()) {
                return newuser.get().getName();
            }

            return null;

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("-> getNamefromToken failed: Token has expired.");
            return null;
        } catch (io.jsonwebtoken.JwtException e) {
            System.out.println("-> getNamefromToken failed: Invalid token structure or signature mismatch.");
            System.out.println("-> Checked Token Value was: " + Token);
            return null;
        }
    }
    public Long getUserIdfromToken(String Token){
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(Token)
                .getPayload();
        return Long.valueOf(claims.getSubject());

    }
    /*public String getUsernameFromToken(String Token){
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(Token)
                .getPayload();
        return claims.getSubject();
    }*/







}
