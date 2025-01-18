package com.example.login.application.dto;

import com.example.login.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder(access = AccessLevel.PRIVATE)
public record UserResponse(
        String username,
        String nickname,
        List<AuthorityDto> authorities
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .authorities(
                        user.getAuthorities().stream()
                                .map(AuthorityDto::of)
                                .collect(Collectors.toList())
                )
                .build();
    }

}
