package com.example.login.infrastructure.repository;

import com.example.login.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User save(User user);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
