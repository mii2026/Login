package com.example.login.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCase implements ErrorCase {
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_ROLE(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자 역할입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}