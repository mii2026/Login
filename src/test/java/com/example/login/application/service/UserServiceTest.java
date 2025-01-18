package com.example.login.application.service;

import com.example.login.common.errors.UserErrorCase;
import com.example.login.common.exception.ApplicationException;
import com.example.login.domain.user.entity.User;
import com.example.login.infrastructure.repository.UserRepository;
import com.example.login.presentation.dto.SignUpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
        userService.createUser(request);

        // Then
        verify(userRepository).existsByUsername("JIN HO");
        verify(passwordEncoder).encode("12341234");
        verify(userRepository).save(any(User.class));
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
                assertThrows(ApplicationException.class, () ->userService.createUser(request));
        assertEquals(UserErrorCase.DUPLICATED_USER.getMessage(), e.getMessage());
    }

}