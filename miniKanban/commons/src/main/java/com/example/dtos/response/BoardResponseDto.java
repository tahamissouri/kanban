package com.example.dtos.response;

import java.util.List;
import java.util.Set;

public record BoardResponseDto(
        Long id,
        String name,
        UserResponseDto owner,
        List<UserResponseDto> members
) {}