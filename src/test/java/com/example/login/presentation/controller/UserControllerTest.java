package com.example.login.presentation.controller;

import com.example.login.application.dto.AuthorityDto;
import com.example.login.application.dto.LoginResponse;
import com.example.login.application.dto.TokenDto;
import com.example.login.application.dto.UserResponse;
import com.example.login.application.service.UserService;
import com.example.login.common.exception.UserErrorCase;
import com.example.login.common.exception.ApplicationException;
import com.example.login.common.exception.GlobalExceptionHandler;
import com.example.login.domain.user.enums.Authority;
import com.example.login.presentation.dto.LoginRequest;
import com.example.login.presentation.dto.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler(objectMapper))
                .build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void createUserTest() throws Exception {
        // Given
        SignUpRequest request = new SignUpRequest("JIN HO", "12341234", "Mentos");
        UserResponse response = new UserResponse(
                "JIN HO",
                "Mentos",
                List.of(AuthorityDto.of(Authority.ROLE_USER))
        );

        doReturn(response)
                .when(userService)
                .createUser(request);

        // Then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username").value("JIN HO"))
                .andExpect(jsonPath("nickname").value("Mentos"))
                .andExpect(jsonPath("$.authorities[0].authorityName").value("ROLE_USER"));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 사용자 이름 없음")
    public void signUpUserNameBlankTest() throws Exception {
        // Given
        SignUpRequest request = new SignUpRequest("", "12341234", "Mentos");

        // Then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("사용자명은 공백일 수 없습니다."));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 사용자 이름 중복")
    public void duplicatedUsernameTest() throws Exception {
        // Given
        SignUpRequest request = new SignUpRequest("JIN HO", "12341234", "Mentos");

        doThrow(new ApplicationException(UserErrorCase.DUPLICATED_USER))
                .when(userService)
                .createUser(any(SignUpRequest.class));

        // Then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 존재하는 유저 이름입니다."));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginTest() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("JIN HO", "12341234");
        LoginResponse response = new LoginResponse("accessToken");
        TokenDto tokenDto = new TokenDto("refreshToken", 100000L, response);

        doReturn(tokenDto)
                .when(userService)
                .login(request);

        // Then
        mockMvc.perform(post("/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").value("accessToken"))
                .andExpect(cookie().value("refresh_token", "refreshToken"));

    }

    @Test
    @DisplayName("로그인 실패 테스트 - 사용자 이름 없음")
    public void loginUserNameBlankTest() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("", "1234");

        // Then
        mockMvc.perform(post("/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("사용자명은 공백일 수 없습니다."));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 존재하지 않는 사용자")
    public void loginUserNotFoundTest() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("JIN HO", "12341234");

        doThrow(new ApplicationException(UserErrorCase.USER_NOT_FOUND))
                .when(userService)
                .login(request);

        // Then
        mockMvc.perform(post("/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("존재하지 않는 사용자입니다."));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    public void wrongPasswordTest() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("JIN HO", "123412345");

        doThrow(new ApplicationException(UserErrorCase.WRONG_PASSWORD))
                .when(userService)
                .login(request);

        // Then
        mockMvc.perform(post("/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("비밀번호가 올바르지 않습니다."));
    }

}