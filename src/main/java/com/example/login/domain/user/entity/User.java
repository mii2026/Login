package com.example.login.domain.user.entity;

import com.example.login.domain.user.enums.Authority;
import com.example.login.presentation.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;
    private String username;
    private String password;
    private String nickname;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Authority> authorities;

    public static User of(SignUpRequest request, String password, Set<Authority> authorities) {
        return User.builder()
                .username(request.username())
                .password(password)
                .nickname(request.nickname())
                .authorities(authorities)
                .build();
    }
}