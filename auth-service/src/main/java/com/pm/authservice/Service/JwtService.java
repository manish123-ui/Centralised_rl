package com.pm.authservice.Service;


import com.pm.authservice.entity.User;
import com.pm.authservice.repositories.UserRepositry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
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
    private UserRepositry userRepositry;
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email",user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSecretKey())
                .compact();
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
