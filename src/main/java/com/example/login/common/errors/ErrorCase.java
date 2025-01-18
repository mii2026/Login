package com.example.login.common.errors;

import org.springframework.http.HttpStatus;

public interface ErrorCase {
    HttpStatus getHttpStatus();
    String getMessage();
}