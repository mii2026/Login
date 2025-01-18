package com.example.login.infrastructure.security;

import com.example.login.common.exception.ApplicationException;
import com.example.login.common.exception.AuthErrorCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {JwtTokenizer.class})
@ActiveProfiles("test")
class JwtTokenizerTest {

    @Autowired
    JwtTokenizer jwtTokenizer;

    @Test
    @DisplayName("Access Token 생성 성공 테스트")
    public void createAccessTokenTest(){
        String accessToken = getAccessToken();

        System.out.println("Generated Access Token: " + accessToken);
        assertThat(accessToken, notNullValue());
    }

    @Test
    @DisplayName("Refresh Token 생성 성공 테스트")
    public void createRefreshToken(){
        String refreshToken = getRefreshToken();

        System.out.println("Generated Refresh Token: " + refreshToken);
        assertThat(refreshToken, notNullValue());
    }

    @Test
    @DisplayName("Access Token 검증 성공 테스트")
    public void validateAccessTokenTest() {
        String accessToken = getAccessToken();
        assertDoesNotThrow(() -> jwtTokenizer.validateAccessToken(accessToken));
    }

    @Test
    @DisplayName("Access Token 검증 실패 테스트 - 유효하지 않은 토큰")
    public void invalidAccessTokenTest() {
        String accessToken = "invalidToken";

        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            jwtTokenizer.validateAccessToken(accessToken);
        });

        assertEquals(AuthErrorCase.INVALID_TOKEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Refresh Token 검증 성공 테스트")
    public void validateRefreshTokenTest() {
        String refreshToken = getRefreshToken();
        assertDoesNotThrow(() -> jwtTokenizer.validateRefreshToken(refreshToken));
    }

    @Test
    @DisplayName("Refresh Token 검증 실패 테스트 - 유효하지 않은 토큰")
    public void invalidRefreshTokenTest() {
        String refreshToken = "invalidToken";

        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            jwtTokenizer.validateRefreshToken(refreshToken);
        });

        assertEquals(AuthErrorCase.INVALID_TOKEN.getMessage(), exception.getMessage());
    }

    private String getAccessToken() {
        UUID userId = UUID.randomUUID();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("ROLE_USER"));

        return jwtTokenizer.createAccessToken(userId.toString(), claims);
    }

    private String getRefreshToken() {
        UUID userId = UUID.randomUUID();
        return jwtTokenizer.createRefreshToken(userId.toString());
    }

}