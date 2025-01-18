package com.example.login.common.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCase implements ErrorCase {
    DUPLICATED_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 유저 이름입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}