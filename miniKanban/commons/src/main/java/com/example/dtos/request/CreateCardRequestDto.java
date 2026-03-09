package com.example.dtos.request;

import jakarta.validation.constraints.NotNull;

public record CreateCardRequestDto(
        @NotNull String title,
        String description,
        int position,
        Long columnId
) {}
