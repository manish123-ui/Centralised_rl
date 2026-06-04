package com.pm.authservice.Service;


import com.pm.authservice.entity.User;
import com.pm.authservice.repositories.UserRepositry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
            // 1. Decode the base64 string
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecretKey);

            // 2. Validate key length (Min 32 bytes for HS256)
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("JWT secret key must be at least 32 bytes (256 bits) long.");
            }

            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            // 3. Catch decoding or length errors
            System.err.println("Failed to generate SecretKey: " + e.getMessage());
            throw e;
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
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
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
    public String getNamefromToken(String Token){
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(Token)
                .getPayload();
        Long id= Long.valueOf(claims.getSubject());
        Optional<User> newuser=userRepositry.findById(id);
        if(newuser.isPresent()){
            return newuser.get().getName();
        }
        return null;
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
