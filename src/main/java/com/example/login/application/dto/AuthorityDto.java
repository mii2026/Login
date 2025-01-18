package com.example.login.application.dto;

import com.example.login.domain.user.enums.Authority;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record AuthorityDto(String authorityName) {

    public static AuthorityDto of(Authority authority){
        return AuthorityDto.builder()
                .authorityName(authority.name())
                .build();
    }

}
