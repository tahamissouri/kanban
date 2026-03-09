package com.example.mappers;

import com.example.persistence.entity.CardEntity;
import com.example.dtos.request.CreateCardRequestDto;

import com.example.dtos.response.CardResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;



@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CardMapper extends MapperContract<CardEntity, CreateCardRequestDto, CardResponseDto> {


    @Mapping(target = "id", ignore = true)
    @Mapping(target="column", ignore = true)
    CardEntity toEntity(CreateCardRequestDto dto);
}
