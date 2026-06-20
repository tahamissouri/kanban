package com.example.api.v1.controller;

import com.example.dtos.request.CreateUserRequestDto;
import com.example.dtos.request.LoginUserRequestDto;
import com.example.dtos.response.TokenResponseDto;
import com.example.dtos.response.UserResponseDto;
import com.example.service.UserService;
import com.example.util.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "User authentication and registration endpoints")
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Register new user",
            description = "Create a new user account and receive a JWT token for authentication"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or user already exists"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> createUser(@RequestBody @Valid CreateUserRequestDto requestDto) {
        UserResponseDto created = userService.createUser(requestDto);
        String token = jwtUtil.generateToken(created.id(), created.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponseDto(token));
    }

    @Operation(
            summary = "User login",
            description = "Authenticate user and receive a JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> loginUser(@RequestBody @Valid LoginUserRequestDto requestDto) {
        UserResponseDto userEntity= userService.login(requestDto);
        String token = jwtUtil.generateToken(userEntity.id(),userEntity.email());
        return ResponseEntity.ok().body(new TokenResponseDto(token));
    }
}
