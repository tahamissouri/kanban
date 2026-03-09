package com.example.api.v1.controller;

import com.example.dtos.request.CreateUserRequestDto;
import com.example.dtos.request.LoginUserRequestDto;
import com.example.dtos.response.TokenResponseDto;
import com.example.dtos.response.UserResponseDto;
import com.example.service.UserService;
import com.example.util.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> createUser(@RequestBody @Valid CreateUserRequestDto requestDto) {
        UserResponseDto created = userService.createUser(requestDto);
        String token = jwtUtil.generateToken(created.id(), created.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponseDto(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> loginUser(@RequestBody @Valid LoginUserRequestDto requestDto) {
        UserResponseDto userEntity= userService.login(requestDto);
        String token = jwtUtil.generateToken(userEntity.id(),userEntity.email());
        return ResponseEntity.ok().body(new TokenResponseDto(token));
    }
}
