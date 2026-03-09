package com.example.mappers;

import com.example.persistence.entity.ColumnEntity;
import com.example.dtos.request.CreateColumnRequestDto;
import com.example.dtos.response.ColumnResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ColumnMapper extends MapperContract<ColumnEntity, CreateColumnRequestDto, ColumnResponseDto> {


    @Mapping(target="id", ignore = true)
    @Mapping(target="board", ignore = true)
    ColumnEntity toEntity(CreateColumnRequestDto columnResquestDto);
}
