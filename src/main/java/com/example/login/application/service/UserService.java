package com.example.login.application.service;

import com.example.login.application.dto.UserResponse;
import com.example.login.common.exception.ApplicationException;
import com.example.login.common.errors.UserErrorCase;
import com.example.login.domain.user.entity.User;
import com.example.login.domain.user.enums.Authority;
import com.example.login.infrastructure.repository.UserRepository;
import com.example.login.presentation.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(SignUpRequest request) {
        if(userRepository.existsByUsername(request.username())){
            throw new ApplicationException(UserErrorCase.DUPLICATED_USER);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.of(request, encodedPassword, Set.of(Authority.ROLE_USER));
        userRepository.save(user);

        return UserResponse.of(user);
    }
}
