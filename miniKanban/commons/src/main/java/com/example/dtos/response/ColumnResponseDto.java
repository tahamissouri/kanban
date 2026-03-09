package com.example.dtos.response;

import java.util.Set;

public record ColumnResponseDto(
        Long id,
        String name,
        int position
) {}