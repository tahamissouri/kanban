package com.example.service;

import com.example.dtos.request.CreateBoardRequestDto;
import com.example.dtos.response.BoardResponseDto;
import com.example.mappers.BoardMapper;
import com.example.persistence.entity.BoardEntity;
import com.example.persistence.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService extends CRUD<BoardRepository, BoardMapper, BoardEntity, CreateBoardRequestDto, BoardResponseDto> {

    private final UserService userService;

    public BoardService(BoardRepository boardRepository, BoardMapper boardMapper, UserService userService) {
        super(boardRepository, boardMapper);
        this.userService = userService;
    }

    public List<BoardResponseDto> getAllByUserId(Long userId) {
        return repository.getAllByOwnerOrUser(userId).stream()
                .map(mapper::toDto).toList();
    }

    public void addMember(Long boardId, Long userId) {
        BoardEntity board = findById(boardId);
        board.getMembers().add(userService.findById(userId));
        repository.save(board);
    }

    public boolean isMember(Object principal, Object boardId) {
        Long userId = ((Number) principal).longValue();
        Long bId    = ((Number) boardId).longValue();
        return findById(bId).getMembers().stream()
                .anyMatch(m -> m.getId().equals(userId));
    }

    public boolean isOwner(Object principal, Object boardId) {
        Long userId = ((Number) principal).longValue();
        Long bId    = ((Number) boardId).longValue();
        return findById(bId).getOwner().getId().equals(userId);
    }

    public boolean isCollaborator(Object principal, Object boardId) {
        return isMember(principal, boardId) || isOwner(principal, boardId);
    }

    public BoardResponseDto create(CreateBoardRequestDto dto, Long ownerId) {
        BoardEntity boardEntity = mapper.toEntity(dto);
        boardEntity.setOwner(userService.findById(ownerId));
        return mapper.toDto(repository.save(boardEntity));
    }
}