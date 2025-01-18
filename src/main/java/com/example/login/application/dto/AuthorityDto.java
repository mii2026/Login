package com.example.login.application.dto;

import com.example.login.domain.user.enums.Authority;

public record AuthorityDto(String authorityName) {

    public static AuthorityDto of(Authority authority){
        return new AuthorityDto(authority.name());
    }

}
