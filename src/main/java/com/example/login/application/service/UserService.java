package com.example.login.application.service;

import com.example.login.application.dto.LoginResponse;
import com.example.login.application.dto.TokenDto;
import com.example.login.application.dto.UserResponse;
import com.example.login.common.exception.ApplicationException;
import com.example.login.common.errors.UserErrorCase;
import com.example.login.domain.user.entity.User;
import com.example.login.domain.user.enums.Authority;
import com.example.login.infrastructure.repository.UserRepository;
import com.example.login.infrastructure.security.JwtTokenizer;
import com.example.login.presentation.dto.LoginRequest;
import com.example.login.presentation.dto.SignUpRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    public UserResponse createUser(SignUpRequest request) {
        if(userRepository.existsByUsername(request.username())){
            throw new ApplicationException(UserErrorCase.DUPLICATED_USER);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.of(request, encodedPassword, Set.of(Authority.ROLE_USER));
        userRepository.save(user);

        return UserResponse.of(user);
    }

    @Transactional
    public TokenDto login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApplicationException(UserErrorCase.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new ApplicationException(UserErrorCase.WRONG_PASSWORD);
        }

        String subject = user.getId().toString();
        Map<String, Object> claims = Map.of(
                "roles", user.getAuthorities().stream()
                                .map(Authority::name)
                                .collect(Collectors.joining(","))
        );

        String accessToken = jwtTokenizer.createAccessToken(subject, claims);
        String refreshToken = jwtTokenizer.createRefreshToken(subject);

        user.updateRefreshToken(refreshToken);
        LoginResponse loginResponse = LoginResponse.of(accessToken);

        return new TokenDto(refreshToken, jwtTokenizer.getRefreshTokenExpiration(), loginResponse);
    }
}
