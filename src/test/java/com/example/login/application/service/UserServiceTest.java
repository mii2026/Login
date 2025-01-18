package com.example.login.application.service;

import com.example.login.application.dto.TokenDto;
import com.example.login.application.dto.UserResponse;
import com.example.login.common.exception.UserErrorCase;
import com.example.login.common.exception.ApplicationException;
import com.example.login.domain.user.entity.User;
import com.example.login.domain.user.enums.Authority;
import com.example.login.infrastructure.repository.UserRepository;
import com.example.login.infrastructure.security.JwtTokenizer;
import com.example.login.presentation.dto.LoginRequest;
import com.example.login.presentation.dto.SignUpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenizer jwtTokenizer;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 가입 성공 테스트")
    public void createUserTest() {
        // Given
        SignUpRequest request = new SignUpRequest("JIN HO", "12341234", "Mentos");

        doReturn(false)
                .when(userRepository)
                .existsByUsername("JIN HO");

        doReturn("encodedPassword")
                .when(passwordEncoder)
                .encode("12341234");

        // When
        UserResponse response = userService.createUser(request);

        // Then
        verify(passwordEncoder).encode("12341234");
        verify(userRepository).save(any(User.class));

        assertEquals("JIN HO", response.username());
        assertEquals("Mentos", response.nickname());
        assertEquals("ROLE_USER", response.authorities().get(0).authorityName());
    }

    @Test
    @DisplayName("회원 가입 실패 테스트 - 중복 사용자 이름")
    public void duplicateUsernameTest() {
        // Given
        SignUpRequest request = new SignUpRequest("JIN HO", "12341234", "Mentos");

        // When
        doReturn(true)
                .when(userRepository)
                .existsByUsername("JIN HO");

        // Then
        ApplicationException e = Assertions.
                assertThrows(ApplicationException.class, () -> userService.createUser(request));
        assertEquals(UserErrorCase.DUPLICATED_USER.getMessage(), e.getMessage());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginTest() {
        // Given
        LoginRequest request = new LoginRequest("JIN HO", "123412345");

        User mockUser = Mockito.mock(User.class);
        when(mockUser.getId()).thenReturn(UUID.fromString("8051ea56-2140-40ff-8be5-107a7723b54e"));
        when(mockUser.getPassword()).thenReturn("encodedPassword");
        when(mockUser.getAuthorities()).thenReturn(Set.of(Authority.ROLE_USER));

        doReturn(Optional.of(mockUser))
                .when(userRepository)
                .findByUsername("JIN HO");

        doReturn(true)
                .when(passwordEncoder)
                .matches("123412345", "encodedPassword");

        doReturn("accessToken")
                .when(jwtTokenizer)
                .createAccessToken("8051ea56-2140-40ff-8be5-107a7723b54e", Map.of("roles", "ROLE_USER"));

        doReturn("refreshToken")
                .when(jwtTokenizer)
                .createRefreshToken("8051ea56-2140-40ff-8be5-107a7723b54e");

        doReturn(10000L)
                .when(jwtTokenizer)
                .getRefreshTokenExpiration();

        // When
        TokenDto tokenDto = userService.login(request);

        // Then
        assertEquals("refreshToken", tokenDto.refreshToken());
        assertEquals(10000L, tokenDto.refreshTokenExpiration());
        assertEquals("accessToken", tokenDto.loginResponse().token());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 존재하지 않는 사용자")
    public void loginUserNotFoundTest() {
        // Given
        LoginRequest request = new LoginRequest("JIN HO", "123412345");

        doReturn(Optional.empty())
                .when(userRepository)
                .findByUsername("JIN HO");

        // Then
        ApplicationException e = Assertions.
                assertThrows(ApplicationException.class, () -> userService.login(request));
        assertEquals(UserErrorCase.USER_NOT_FOUND.getMessage(), e.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    public void wrongPasswordTest() {
        // Given
        LoginRequest request = new LoginRequest("JIN HO", "123412345");

        User mockUser = Mockito.mock(User.class);
        when(mockUser.getPassword()).thenReturn("encodedPassword");

        doReturn(Optional.of(mockUser))
                .when(userRepository)
                .findByUsername("JIN HO");

        doReturn(false)
                .when(passwordEncoder)
                .matches("123412345", "encodedPassword");

        // Then
        ApplicationException e = Assertions.
                assertThrows(ApplicationException.class, () -> userService.login(request));
        assertEquals(UserErrorCase.WRONG_PASSWORD.getMessage(), e.getMessage());
    }

}