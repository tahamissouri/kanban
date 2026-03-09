package com.example.mappers;

public interface MapperContract<E,DtoRequest,DtoResponse> {
    DtoResponse toDto(E e);
    E toEntity(DtoRequest dtoRequest);
}
