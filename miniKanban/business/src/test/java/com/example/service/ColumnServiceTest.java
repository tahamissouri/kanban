package com.example.service;

import com.example.dtos.request.CreateColumnRequestDto;
import com.example.dtos.response.ColumnResponseDto;
import com.example.exception.AppException;
import com.example.exception.ExceptionMessages;
import com.example.mappers.ColumnMapper;
import com.example.persistence.entity.BoardEntity;
import com.example.persistence.entity.ColumnEntity;
import com.example.persistence.repository.ColumnRepository;
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
class ColumnServiceTest {

    @Mock ColumnRepository columnRepository;
    @Mock ColumnMapper     columnMapper;
    @Mock BoardService     boardService;

    @InjectMocks ColumnService columnService;

    private BoardEntity       board;
    private ColumnEntity      column;
    private ColumnResponseDto columnDto;

    @BeforeEach
    void setUp() {
        board = new BoardEntity();
        board.setId(1L);

        column = new ColumnEntity();
        column.setId(1L);
        column.setName("To Do");
        column.setPosition(1);
        column.setBoard(board);

        columnDto = new ColumnResponseDto(1L, "To Do", 1);
    }

    // ── createColumn ──────────────────────────────────────────────────────

    @Test
    @DisplayName("createColumn: success — board is set on entity before save")
    void createColumn_success() {
        var dto = new CreateColumnRequestDto("To Do", 1);
        when(columnMapper.toEntity(dto)).thenReturn(column);
        when(boardService.findById(1L)).thenReturn(board);
        when(columnRepository.save(column)).thenReturn(column);
        when(columnMapper.toDto(column)).thenReturn(columnDto);

        var result = columnService.createColumn(dto, 1L);

        assertThat(result.name()).isEqualTo("To Do");
        verify(columnRepository).save(argThat(c -> c.getBoard().equals(board)));
    }

    @Test
    @DisplayName("createColumn: throws when board not found")
    void createColumn_boardNotFound_throws() {
        var dto = new CreateColumnRequestDto("To Do", 1);
        when(columnMapper.toEntity(dto)).thenReturn(column);
        when(boardService.findById(99L))
                .thenThrow(new AppException(ExceptionMessages.ENTITY_NOT_FOUND, "Board", 99L));

        assertThatThrownBy(() -> columnService.createColumn(dto, 99L))
                .isInstanceOf(AppException.class);
        verify(columnRepository, never()).save(any());
    }

    // ── getColumnsByBoard ─────────────────────────────────────────────────

    @Test
    @DisplayName("getColumnsByBoard: returns columns ordered by position")
    void getColumnsByBoard_returnsOrdered() {
        when(columnRepository.findByBoardIdOrderByPosition(1L)).thenReturn(List.of(column));
        when(columnMapper.toDto(column)).thenReturn(columnDto);

        assertThat(columnService.getColumnsByBoard(1L)).hasSize(1);
    }

    @Test
    @DisplayName("getColumnsByBoard: returns empty list when board has no columns")
    void getColumnsByBoard_empty() {
        when(columnRepository.findByBoardIdOrderByPosition(1L)).thenReturn(List.of());
        assertThat(columnService.getColumnsByBoard(1L)).isEmpty();
    }

    // ── updateColumn ──────────────────────────────────────────────────────

    @Test
    @DisplayName("updateColumn: success — updates name and position")
    void updateColumn_success() {
        var dto     = new CreateColumnRequestDto("In Progress", 2);
        var updated = new ColumnResponseDto(1L, "In Progress", 2);
        when(columnRepository.findById(1L)).thenReturn(Optional.of(column));
        when(columnRepository.save(column)).thenReturn(column);
        when(columnMapper.toDto(column)).thenReturn(updated);

        var result = columnService.updateColumn(dto, 1L);

        assertThat(result.name()).isEqualTo("In Progress");
        assertThat(result.position()).isEqualTo(2);
    }

    @Test
    @DisplayName("updateColumn: throws when column not found")
    void updateColumn_notFound_throws() {
        var dto = new CreateColumnRequestDto("In Progress", 2);
        when(columnRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> columnService.updateColumn(dto, 99L))
                .isInstanceOf(AppException.class);
    }

    // ── delete ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete: success — calls deleteById")
    void delete_success() {
        when(columnRepository.existsById(1L)).thenReturn(true);
        columnService.delete(1L);
        verify(columnRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete: throws when column not found")
    void delete_notFound_throws() {
        when(columnRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> columnService.delete(99L))
                .isInstanceOf(AppException.class);
        verify(columnRepository, never()).deleteById(any());
    }
}
