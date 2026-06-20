package com.example.api.v1.controller;

import com.example.dtos.request.CreateBoardRequestDto;
import com.example.dtos.response.BoardResponseDto;
import com.example.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Boards", description = "Board management endpoints")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/boards")
@RestController
public class BoardController {
    final private BoardService boardService;


    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @Operation(
            summary = "Get all user boards",
            description = "Retrieve all boards where the authenticated user is owner or member"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved boards",
                    content = @Content(schema = @Schema(implementation = BoardResponseDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoardsByUser(Authentication authentication) {
        Long userID = (Long) authentication.getPrincipal();
        return ResponseEntity.ok().body(boardService.getAllByUserId(userID));
    }
    
    @Operation(
            summary = "Create new board",
            description = "Create a new Kanban board. The authenticated user becomes the owner."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Board successfully created",
                    content = @Content(schema = @Schema(implementation = BoardResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            Authentication authentication,
            @RequestBody CreateBoardRequestDto boardRequestDto) {
        Long userID = (Long) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.create(boardRequestDto,userID));
    }

    @Operation(
            summary = "Delete board",
            description = "Delete a board. Only the board owner can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not the board owner"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @PreAuthorize("@boardService.isOwner(authentication.getPrincipal(),#boardId)")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @Parameter(description = "Board ID", required = true)
            @PathVariable Long boardId) {
        boardService.delete(boardId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Add member to board",
            description = "Add a user as a member to the board. Only the board owner can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member successfully added"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not the board owner"),
            @ApiResponse(responseCode = "404", description = "Board or user not found")
    })
    @PreAuthorize("@boardService.isOwner(authentication.principal, #boardId)")
    @PatchMapping("/{boardId}/members/{userId}")
    public ResponseEntity<Void> addMember(
            @Parameter(description = "Board ID", required = true)
            @PathVariable Long boardId,
            @Parameter(description = "User ID to add as member", required = true)
            @PathVariable Long userId) {
        boardService.addMember(boardId, userId);
        return ResponseEntity.ok().build();
    }

}
