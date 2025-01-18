package com.example.login.presentation.controller;

import com.example.login.application.dto.LoginResponse;
import com.example.login.application.dto.TokenDto;
import com.example.login.application.dto.UserResponse;
import com.example.login.application.service.UserService;
import com.example.login.presentation.dto.LoginRequest;
import com.example.login.presentation.dto.SignUpRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @PostMapping("/sign")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request,
                                               HttpServletResponse response) {
        TokenDto tokenDto = userService.login(request);

        setRefreshTokenCookie(response, tokenDto.refreshToken(), tokenDto.refreshTokenExpiration());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokenDto.loginResponse());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, long expiration) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (expiration / 1000));
        response.addCookie(cookie);
    }
}
