package com.example.mappers;


import com.example.persistence.entity.BoardEntity;
import com.example.dtos.request.CreateBoardRequestDto;
import com.example.dtos.response.BoardResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {UserMapper.class}
)
public interface BoardMapper extends MapperContract<BoardEntity,CreateBoardRequestDto,BoardResponseDto> {

    BoardResponseDto toDto(BoardEntity boardEntity);

    @Mapping(target="id", ignore = true)
    @Mapping(target="createdAt", ignore = true)
    @Mapping(target = "members",ignore = true)
    @Mapping(target="owner", ignore=true)
    BoardEntity toEntity(CreateBoardRequestDto boardRequestDto);
}
