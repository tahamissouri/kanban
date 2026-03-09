package com.example.dtos.request;

import jakarta.validation.constraints.*;


public record CreateBoardRequestDto(
        @NotNull String name
) {}