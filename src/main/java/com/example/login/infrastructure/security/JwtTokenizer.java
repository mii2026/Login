package com.example.login.infrastructure.security;

import com.example.login.common.errors.AuthErrorCase;
import com.example.login.common.exception.ApplicationException;
import com.example.login.domain.user.enums.Authority;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenizer {

    @Getter
    private final long refreshTokenExpiration;
    private final long accessTokenExpiration;

    private final Key accessKey;
    private final Key refreshKey;

    public JwtTokenizer(
            @Value("${jwt.accessToken.secret}") String accessSecret,
            @Value("${jwt.refreshToken.secret}") String refreshSecret,
            @Value("${jwt.accessToken.expiration}") long accessTokenExpiration,
            @Value("${jwt.refreshToken.expiration}") long refreshTokenExpiration
    ) {
        byte[] bytes = Base64.getDecoder().decode(accessSecret);
        this.accessKey = Keys.hmacShaKeyFor(bytes);

        bytes = Base64.getDecoder().decode(refreshSecret);
        this.refreshKey = Keys.hmacShaKeyFor(bytes);

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
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
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ApplicationException(AuthErrorCase.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new ApplicationException(AuthErrorCase.INVALID_TOKEN);
        }
    }

    public UUID getUserIdFromAccessToken(String token){
        try {
            String userId = Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(AuthErrorCase.INVALID_TOKEN);
        }
    }

    public Set<Authority> getAuthoritiesFromAccessToken(String token) {
        String roles = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", String.class);

        return Arrays.stream(roles.split(","))
                .map(Authority::getAuthority)
                .collect(Collectors.toSet());
    }

}