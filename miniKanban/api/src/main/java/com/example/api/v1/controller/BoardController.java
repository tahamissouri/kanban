package com.example.api.v1.controller;

import com.example.dtos.request.CreateBoardRequestDto;
import com.example.dtos.response.BoardResponseDto;
import com.example.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/boards")
@RestController
public class BoardController {
    final private BoardService boardService;


    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoardsByUser(Authentication authentication) {
        Long userID = (Long) authentication.getPrincipal();
        return ResponseEntity.ok().body(boardService.getAllByUserId(userID));
    }
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(Authentication authentication,@RequestBody CreateBoardRequestDto boardRequestDto) {
        Long userID = (Long) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.create(boardRequestDto,userID));
    }


    @PreAuthorize("@boardService.isOnwer(authentication.getPrincipal(),#boardId)")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.delete(boardId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@boardService.isOnwer(authentication.principal, #boardId)")
    @PatchMapping("/{boardId}/members/{userId}")
    public ResponseEntity<Void> addMember(@PathVariable Long boardId, @PathVariable Long userId) {
        boardService.addMember(boardId, userId);
        return ResponseEntity.ok().build();
    }

}
