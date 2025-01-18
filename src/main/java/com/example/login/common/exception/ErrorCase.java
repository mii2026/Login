package com.example.login.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCase {
    HttpStatus getHttpStatus();
    String getMessage();
}