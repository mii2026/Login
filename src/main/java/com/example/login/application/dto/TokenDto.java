package com.example.login.application.dto;

public record TokenDto (
        String refreshToken,
        Long refreshTokenExpiration,
        LoginResponse loginResponse
){ }
