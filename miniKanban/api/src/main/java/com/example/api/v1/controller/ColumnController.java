
package com.example.api.v1.controller;

import com.example.dtos.request.CreateColumnRequestDto;
import com.example.dtos.response.ColumnResponseDto;
import com.example.service.ColumnService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/boards/{boardId}/columns")
@RestController
@PreAuthorize("@boardService.isCollaborator(authentication.principal,#boardId)")
public class ColumnController {
    final private ColumnService columnService;

    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }

    @PostMapping()
    public ResponseEntity<ColumnResponseDto> createColumn(@RequestBody CreateColumnRequestDto columnRequestDto,
                                                          @PathVariable("boardId") Long boardId) {
        return ResponseEntity.status(201).body(columnService.createColumn(columnRequestDto, boardId));
    }

    @GetMapping()
    public ResponseEntity<List<ColumnResponseDto>> getAllColumns(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok().body(columnService.getColumnsByBoard(boardId));
    }

    @DeleteMapping("/{idColumn}")
    public ResponseEntity<Void> deleteColumn(@PathVariable Long idColumn, @PathVariable("boardId") Long boardId) {
        columnService.delete(idColumn);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{columnId}")
    public ResponseEntity<ColumnResponseDto> updateColumn(@RequestBody CreateColumnRequestDto columnRequestDto,
                                                          @PathVariable Long columnId,
                                                          @PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok().body(columnService.updateColumn(columnRequestDto, columnId));
    }
}
