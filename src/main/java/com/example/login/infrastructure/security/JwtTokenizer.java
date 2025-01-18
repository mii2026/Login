package com.example.login.infrastructure.security;

import com.example.login.common.exception.ApplicationException;
import com.example.login.common.errors.AuthErrorCase;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenizer {

    @Value("${jwt.accessToken.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refreshToken.expiration}")
    private long refreshTokenExpiration;

    private Key accessKey;
    private Key refreshKey;

    public JwtTokenizer(
            @Value("${jwt.accessToken.secret}") String accessSecret,
            @Value("${jwt.refreshToken.secret}") String refreshSecret
    ) {
        byte[] bytes = Base64.getDecoder().decode(accessSecret);
        this.accessKey = Keys.hmacShaKeyFor(bytes);

        bytes = Base64.getDecoder().decode(refreshSecret);
        this.refreshKey = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpiration))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String subject) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiration))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateAccessToken(String token) {
        validateToken(token, accessKey);
    }

    public void validateRefreshToken(String token) {
        validateToken(token, refreshKey);
    }

    private void validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ApplicationException(AuthErrorCase.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new ApplicationException(AuthErrorCase.INVALID_TOKEN);
        }
    }

}