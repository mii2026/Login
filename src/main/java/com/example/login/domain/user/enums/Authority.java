package com.example.login.domain.user.enums;

import com.example.login.common.errors.AuthErrorCase;
import com.example.login.common.exception.ApplicationException;

public enum Authority {
    ROLE_USER,
    ROLE_ADMIN;

    public static Authority getAuthority(final String value) {
        try {
            return Authority.valueOf(value);
        } catch (final IllegalArgumentException e) {
            throw new ApplicationException(AuthErrorCase.INVALID_ROLE);
        }
    }
}
