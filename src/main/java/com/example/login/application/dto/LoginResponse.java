package com.example.login.application.dto;

public record LoginResponse(String token) {

    public static LoginResponse of(String token) {
        return new LoginResponse(token);
    }

}
