package com.example.dtos.response;

public record CardResponseDto(
        Long id,
        String title,
        String description,
        int position
) {}