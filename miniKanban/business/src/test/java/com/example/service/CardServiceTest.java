package com.example.service;

import com.example.dtos.request.CreateCardRequestDto;
import com.example.dtos.response.CardResponseDto;
import com.example.exception.AppException;
import com.example.exception.ExceptionMessages;
import com.example.mappers.CardMapper;
import com.example.persistence.entity.CardEntity;
import com.example.persistence.entity.ColumnEntity;
import com.example.persistence.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock CardRepository cardRepository;
    @Mock CardMapper     cardMapper;
    @Mock ColumnService  columnService;

    @InjectMocks CardService cardService;

    private ColumnEntity    column;
    private CardEntity      card;
    private CardResponseDto cardDto;

    @BeforeEach
    void setUp() {
        column = new ColumnEntity();
        column.setId(1L);
        column.setName("To Do");
        column.setPosition(1);

        card = new CardEntity();
        card.setId(1L);
        card.setTitle("Fix bug");
        card.setDescription("Critical fix");
        card.setPosition(0);
        card.setColumn(column);

        cardDto = new CardResponseDto(1L, "Fix bug", "Critical fix", 0);
    }

    // ── createCard ────────────────────────────────────────────────────────

    @Test
    @DisplayName("createCard: success — column is fetched and set on entity")
    void createCard_success() {
        var dto = new CreateCardRequestDto("Fix bug", "Critical fix", 0,0L);
        when(columnService.findById(1L)).thenReturn(column);
        when(cardMapper.toEntity(dto)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        var result = cardService.createCard(1L, dto);

        assertThat(result.title()).isEqualTo("Fix bug");
        verify(cardRepository).save(argThat(c -> c.getColumn().equals(column)));
    }

    @Test
    @DisplayName("createCard: throws 404 when column not found")
    void createCard_columnNotFound_throws() {
        var dto = new CreateCardRequestDto("Fix bug", "desc", 0,0L);
        when(columnService.findById(99L))
                .thenThrow(new AppException(ExceptionMessages.ENTITY_NOT_FOUND, "Column", 99L));

        assertThatThrownBy(() -> cardService.createCard(99L, dto))
                .isInstanceOf(AppException.class);
        verify(cardRepository, never()).save(any());
    }

    // ── updateCard ────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateCard: success — all fields updated")
    void updateCard_success() {
        var dto     = new CreateCardRequestDto("Updated", "New desc", 2,0L);
        var updated = new CardResponseDto(1L, "Updated", "New desc", 2);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(updated);

        var result = cardService.updateCard(1L, dto);

        assertThat(result.title()).isEqualTo("Updated");
        assertThat(result.position()).isEqualTo(2);
    }

    @Test
    @DisplayName("updateCard: throws when card not found")
    void updateCard_notFound_throws() {
        var dto = new CreateCardRequestDto("Title", "desc", 0,0L);
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.updateCard(99L, dto))
                .isInstanceOf(AppException.class);
    }

    // ── getAllCardsByColumnId ──────────────────────────────────────────────

    @Test
    @DisplayName("getAllCardsByColumnId: returns cards for column")
    void getAllCardsByColumnId_success() {
        when(columnService.existsById(1L)).thenReturn(true);
        when(cardRepository.getAllByColumnId(1L)).thenReturn(List.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        assertThat(cardService.getAllCardsByColumnId(1L)).hasSize(1);
    }

    @Test
    @DisplayName("getAllCardsByColumnId: throws 404 when column does not exist (fix verified)")
    void getAllCardsByColumnId_columnNotFound_throws() {
        when(columnService.existsById(99L)).thenReturn(false);

        // After fix: existsById false → throws AppException, not silently returns empty
        assertThatThrownBy(() -> cardService.getAllCardsByColumnId(99L))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("getAllCardsByColumnId: returns empty list when column has no cards")
    void getAllCardsByColumnId_empty() {
        when(columnService.existsById(1L)).thenReturn(true);
        when(cardRepository.getAllByColumnId(1L)).thenReturn(List.of());

        assertThat(cardService.getAllCardsByColumnId(1L)).isEmpty();
    }

    // ── delete ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete: success — calls deleteById")
    void delete_success() {
        when(cardRepository.existsById(1L)).thenReturn(true);
        cardService.delete(1L);
        verify(cardRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete: throws when card not found")
    void delete_notFound_throws() {
        when(cardRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> cardService.delete(99L))
                .isInstanceOf(AppException.class);
        verify(cardRepository, never()).deleteById(any());
    }
}
