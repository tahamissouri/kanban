package com.example.service;

import com.example.dtos.request.CreateColumnRequestDto;
import com.example.dtos.response.ColumnResponseDto;
import com.example.mappers.ColumnMapper;
import com.example.persistence.entity.ColumnEntity;
import com.example.persistence.repository.ColumnRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ColumnService extends CRUD<ColumnRepository,ColumnMapper,ColumnEntity,CreateColumnRequestDto,ColumnResponseDto> {


    final private BoardService  boardService;

    public ColumnService(ColumnRepository columnRepository, ColumnMapper columnMapper, BoardService boardService) {
        super(columnRepository, columnMapper);
        this.boardService = boardService;
    }


    public ColumnResponseDto createColumn(CreateColumnRequestDto createColumnRequestDto,Long boardID) {
        ColumnEntity columnEntity = mapper.toEntity(createColumnRequestDto);
        columnEntity.setBoard(boardService.findById(boardID));
        return mapper.toDto(repository.save(columnEntity));
    }

    public List<ColumnResponseDto> getColumnsByBoard(Long idBoard) {
       return  repository.findByBoardIdOrderByPosition(idBoard).stream()
               .map(mapper::toDto).toList();
    }


    public ColumnResponseDto updateColumn(CreateColumnRequestDto columnRequestDto, Long columnId) {
        ColumnEntity columnEntity = findById(columnId);
        columnEntity.setName(columnRequestDto.name());
        columnEntity.setPosition(columnRequestDto.position());
        return this.save(columnEntity);
    }

}
