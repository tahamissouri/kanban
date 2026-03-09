package com.example.api.v1.controller;






import com.example.dtos.request.CreateCardRequestDto;
import com.example.dtos.response.CardResponseDto;
import com.example.service.CardService;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/boards/{boardId}/columns/{columnId}/cards")
@RestController
@PreAuthorize("@boardService.isCollaborator(authentication.principal,#boardId)")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping()
    public ResponseEntity<CardResponseDto> createCard(@RequestBody @Valid CreateCardRequestDto createCardRequestDto,
                                                      @PathVariable Long columnId,
                                                      @PathVariable Long boardId) {
        return ResponseEntity.ok(cardService.createCard(columnId, createCardRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponseDto> updateCard(@RequestBody @Valid CreateCardRequestDto createCardRequestDto
            , @PathVariable Long id,@PathVariable Long boardId) {
       return ResponseEntity.ok().body(cardService.updateCard(id,createCardRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id,@PathVariable Long boardId) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping()
    public ResponseEntity<List<CardResponseDto>> getCards(@PathVariable Long columnId,@PathVariable Long boardId) {
        return ResponseEntity.ok().body(cardService.getAllCardsByColumnId(columnId));
    }


}
