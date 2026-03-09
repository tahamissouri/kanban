package com.example.dtos.request;

import jakarta.validation.constraints.*;

public record CreateColumnRequestDto(
        @NotNull
        String name,
        @NotNull
        @PositiveOrZero
        int position
) {}
