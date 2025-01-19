package com.example.login.application.service;

import com.example.login.application.dto.LoginResponse;
import com.example.login.application.dto.TokenDto;
import com.example.login.application.dto.UserResponse;
import com.example.login.common.exception.ApplicationException;
import com.example.login.common.exception.UserErrorCase;
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
        validateDuplicateUser(request.username());

        String encodedPassword = encodePassword(request.password());
        User user = createUserEntity(request, encodedPassword);
        userRepository.save(user);

        return UserResponse.of(user);
    }

    @Transactional
    public TokenDto login(LoginRequest request) {
        User user = findUserByUsername(request.username());
        validatePassword(request.password(), user.getPassword());

        String subject = user.getId().toString();
        Map<String, Object> claims = createClaims(user);
        String accessToken = jwtTokenizer.createAccessToken(subject, claims);
        String refreshToken = jwtTokenizer.createRefreshToken(subject);

        user.updateRefreshToken(refreshToken);

        return createTokenDto(refreshToken, accessToken);
    }

    private void validateDuplicateUser(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new ApplicationException(UserErrorCase.DUPLICATED_USER);
        }
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private User createUserEntity(SignUpRequest request, String encodedPassword) {
        return User.of(request, encodedPassword, Set.of(Authority.ROLE_USER));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.USER_NOT_FOUND));
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ApplicationException(UserErrorCase.WRONG_PASSWORD);
        }
    }

    private Map<String, Object> createClaims(User user) {
        return Map.of(
                "roles", user.getAuthorities().stream()
                        .map(Authority::name)
                        .collect(Collectors.joining(","))
        );
    }

    private TokenDto createTokenDto(String refreshToken, String accessToken) {
        LoginResponse loginResponse = LoginResponse.of(accessToken);
        return new TokenDto(refreshToken, jwtTokenizer.getRefreshTokenExpiration(), loginResponse);
    }
}
