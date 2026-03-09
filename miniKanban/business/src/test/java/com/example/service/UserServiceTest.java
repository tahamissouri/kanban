package com.example.service;

import com.example.dtos.request.CreateUserRequestDto;
import com.example.dtos.request.LoginUserRequestDto;
import com.example.dtos.response.UserResponseDto;
import com.example.exception.AppException;
import com.example.mappers.UserMapper;
import com.example.persistence.entity.UserEntity;
import com.example.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository  userRepository;
    @Mock UserMapper      userMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    private UserEntity      entity;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        entity = new UserEntity();
        entity.setId(1L);
        entity.setUserName("taha");
        entity.setEmail("taha@gmail.com");
        entity.setPassword("$2a$12$hashed");
        responseDto = new UserResponseDto(1L, "taha", "taha@gmail.com");
    }

    // ── createUser ────────────────────────────────────────────────────────

    @Test
    @DisplayName("createUser: success — encodes password and returns DTO")
    void createUser_success() {
        var dto = new CreateUserRequestDto("taha", "taha@gmail.com", "password123");
        when(userRepository.existsByUserName("taha")).thenReturn(false);
        when(userRepository.existsByEmail("taha@gmail.com")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(entity);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$hashed");
        when(userRepository.save(any())).thenReturn(entity);
        when(userMapper.toDto(entity)).thenReturn(responseDto);

        var result = userService.createUser(dto);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("taha@gmail.com");
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("createUser: raw password is never stored in DB")
    void createUser_rawPasswordNeverStored() {
        var dto = new CreateUserRequestDto("taha", "taha@gmail.com", "password123");
        when(userRepository.existsByUserName(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(entity);
        when(passwordEncoder.encode(any())).thenReturn("$2a$12$hashed");
        when(userRepository.save(any())).thenReturn(entity);
        when(userMapper.toDto(any())).thenReturn(responseDto);

        userService.createUser(dto);

        verify(userRepository).save(argThat(u -> !u.getPassword().equals("password123")));
    }

    @Test
    @DisplayName("createUser: throws when username already taken")
    void createUser_usernameTaken_throws() {
        var dto = new CreateUserRequestDto("taha", "taha@gmail.com", "password123");
        when(userRepository.existsByUserName("taha")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("exception.username.exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createUser: throws when email already taken")
    void createUser_emailTaken_throws() {
        var dto = new CreateUserRequestDto("taha", "taha@gmail.com", "password123");
        when(userRepository.existsByUserName("taha")).thenReturn(false);
        when(userRepository.existsByEmail("taha@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("exception.email.exists");

        verify(userRepository, never()).save(any());
    }

    // ── login ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: success — returns user DTO")
    void login_success() {
        var dto = new LoginUserRequestDto("taha@gmail.com", "password123");
        when(userRepository.findByEmail("taha@gmail.com")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("password123", "$2a$12$hashed")).thenReturn(true);
        when(userMapper.toDto(entity)).thenReturn(responseDto);

        assertThat(userService.login(dto).email()).isEqualTo("taha@gmail.com");
    }

    @Test
    @DisplayName("login: throws when email not found")
    void login_emailNotFound_throws() {
        var dto = new LoginUserRequestDto("nobody@gmail.com", "password123");
        when(userRepository.findByEmail("nobody@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("exception.access.denied");
    }

    @Test
    @DisplayName("login: throws when password is wrong")
    void login_wrongPassword_throws() {
        var dto = new LoginUserRequestDto("taha@gmail.com", "wrongpass");
        when(userRepository.findByEmail("taha@gmail.com")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("wrongpass", "$2a$12$hashed")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("exception.access.denied");
    }
}
