package com.example.login.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank(message = "사용자명은 공백일 수 없습니다.")
        String username,

        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        String password,

        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        String nickname
) { }
