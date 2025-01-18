package com.example.login.common.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCase implements ErrorCase {
    DUPLICATED_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 유저 이름입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}