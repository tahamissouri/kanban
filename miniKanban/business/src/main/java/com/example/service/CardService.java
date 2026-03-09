package com.example.service;

import com.example.exception.AppException;
import com.example.exception.ExceptionMessages;
import com.example.dtos.request.CreateCardRequestDto;
import com.example.dtos.response.CardResponseDto;
import com.example.mappers.CardMapper;
import com.example.persistence.entity.CardEntity;
import com.example.persistence.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CardService extends CRUD<CardRepository,CardMapper,CardEntity,CreateCardRequestDto, CardResponseDto> {

    private final ColumnService columnService;

    CardService(CardRepository cardRepository,CardMapper cardMapper,ColumnService columnService) {

        super(cardRepository, cardMapper);
        this.columnService = columnService;

    }

    public CardResponseDto updateCard(Long id,CreateCardRequestDto createCardRequestDto) {
        CardEntity cardEntity = findById(id);
        cardEntity.setPosition(createCardRequestDto.position());
        cardEntity.setTitle(createCardRequestDto.title());
        cardEntity.setDescription(createCardRequestDto.description());
        cardEntity.setColumn(columnService.findById(createCardRequestDto.columnId()));
        return save(cardEntity);
    }

    public List<CardResponseDto> getAllCardsByColumnId(Long columnId) {
        if (!columnService.existsById(columnId)) {
            throw new AppException(ExceptionMessages.ENTITY_NOT_FOUND, "Column", columnId);
        }
        return repository.getAllByColumnId(columnId).stream()
                .map(mapper::toDto).toList();
    }

    public CardResponseDto createCard(Long columnId, CreateCardRequestDto createCardRequestDto) {
        CardEntity cardEntity = mapper.toEntity(createCardRequestDto);
        cardEntity.setColumn(columnService.findById(columnId));
        return save(cardEntity);
    }
}
