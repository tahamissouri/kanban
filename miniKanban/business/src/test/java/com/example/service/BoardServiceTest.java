package com.example.service;

import com.example.dtos.request.CreateBoardRequestDto;
import com.example.dtos.response.BoardResponseDto;
import com.example.dtos.response.UserResponseDto;
import com.example.exception.AppException;
import com.example.exception.ExceptionMessages;
import com.example.mappers.BoardMapper;
import com.example.persistence.entity.BoardEntity;
import com.example.persistence.entity.UserEntity;
import com.example.persistence.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock BoardRepository boardRepository;
    @Mock BoardMapper     boardMapper;
    @Mock UserService     userService;

    @InjectMocks BoardService boardService;

    private UserEntity       owner;
    private UserEntity       member;
    private BoardEntity      board;
    private BoardResponseDto boardDto;

    @BeforeEach
    void setUp() {
        owner = new UserEntity();
        owner.setId(1L);
        owner.setUserName("taha");
        owner.setEmail("taha@gmail.com");

        member = new UserEntity();
        member.setId(2L);
        member.setUserName("alice");
        member.setEmail("alice@gmail.com");

        board = new BoardEntity();
        board.setId(1L);
        board.setName("My Board");
        board.setOwner(owner);
        board.setMembers(new HashSet<>(Set.of(member)));

        boardDto = new BoardResponseDto(
                1L, "My Board",
                new UserResponseDto(1L, "taha", "taha@gmail.com"),
                List.of(new UserResponseDto(2L, "alice", "alice@gmail.com"))
        );
    }

    // ── create ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create: success — owner is set on entity before save")
    void create_success() {
        var dto = new CreateBoardRequestDto("My Board");
        when(boardMapper.toEntity(dto)).thenReturn(board);
        when(userService.findById(1L)).thenReturn(owner);
        when(boardRepository.save(board)).thenReturn(board);
        when(boardMapper.toDto(board)).thenReturn(boardDto);

        var result = boardService.create(dto, 1L);

        assertThat(result.name()).isEqualTo("My Board");
        verify(boardRepository).save(argThat(b -> b.getOwner().equals(owner)));
    }

    @Test
    @DisplayName("create: throws 404 when owner not found")
    void create_ownerNotFound_throws() {
        var dto = new CreateBoardRequestDto("My Board");
        when(boardMapper.toEntity(dto)).thenReturn(board);
        when(userService.findById(99L))
                .thenThrow(new AppException(ExceptionMessages.ENTITY_NOT_FOUND, "User", 99L));

        assertThatThrownBy(() -> boardService.create(dto, 99L))
                .isInstanceOf(AppException.class);
        verify(boardRepository, never()).save(any());
    }

    // ── getAllByUserId ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllByUserId: returns boards where user is owner or member")
    void getAllByUserId_returnsBoards() {
        when(boardRepository.getAllByOwnerOrUser(1L)).thenReturn(List.of(board));
        when(boardMapper.toDto(board)).thenReturn(boardDto);

        assertThat(boardService.getAllByUserId(1L)).hasSize(1);
    }

    @Test
    @DisplayName("getAllByUserId: returns empty list when user has no boards")
    void getAllByUserId_empty() {
        when(boardRepository.getAllByOwnerOrUser(99L)).thenReturn(List.of());

        assertThat(boardService.getAllByUserId(99L)).isEmpty();
    }

    // ── addMember ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("addMember: success — saves board after adding member")
    void addMember_savesBoard() {
        var newMember = new UserEntity();
        newMember.setId(3L);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(userService.findById(3L)).thenReturn(newMember);
        when(boardRepository.save(board)).thenReturn(board);

        boardService.addMember(1L, 3L);

        assertThat(board.getMembers()).contains(newMember);
        verify(boardRepository).save(board); // fix 2: must persist
    }

    @Test
    @DisplayName("addMember: throws when board not found")
    void addMember_boardNotFound_throws() {
        when(boardRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> boardService.addMember(99L, 1L))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("addMember: throws when user not found")
    void addMember_userNotFound_throws() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(userService.findById(99L))
                .thenThrow(new AppException(ExceptionMessages.ENTITY_NOT_FOUND, "User", 99L));
        assertThatThrownBy(() -> boardService.addMember(1L, 99L))
                .isInstanceOf(AppException.class);
    }

    // ── isOwner ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("isOnwer: returns true when principal matches board owner")
    void isOwner_true() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        assertThat(boardService.isOwner((Object) 1L, (Object) 1L)).isTrue();
    }

    @Test
    @DisplayName("isOnwer: returns false when principal is different from owner")
    void isOwner_false() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        assertThat(boardService.isOwner((Object) 2L, (Object) 1L)).isFalse();
    }

    // ── isMember (fixed) ──────────────────────────────────────────────────

    @Test
    @DisplayName("isMember: returns true when user is in members set (fix verified)")
    void isMember_true() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        assertThat(boardService.isMember((Object) 2L, (Object) 1L)).isTrue();
    }

    @Test
    @DisplayName("isMember: returns false when user is not in members set")
    void isMember_false() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        assertThat(boardService.isMember((Object) 99L, (Object) 1L)).isFalse();
    }

    // ── isCollaborator ────────────────────────────────────────────────────

    @Test
    @DisplayName("isCollaborator: true when user is owner")
    void isCollaborator_owner() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        assertThat(boardService.isCollaborator((Object) 1L, (Object) 1L)).isTrue();
    }

    @Test
    @DisplayName("isCollaborator: true when user is a member")
    void isCollaborator_member() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board), Optional.of(board));
        assertThat(boardService.isCollaborator((Object) 2L, (Object) 1L)).isTrue();
    }

    @Test
    @DisplayName("isCollaborator: false when user is neither owner nor member")
    void isCollaborator_stranger() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board), Optional.of(board));
        assertThat(boardService.isCollaborator((Object) 99L, (Object) 1L)).isFalse();
    }
}
