package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.BoardCreateRequest;
import com.example.hanaharmonybackend.web.dto.BoardNearbyDto;
import com.example.hanaharmonybackend.web.dto.BoardResponse;
import com.example.hanaharmonybackend.web.dto.BoardUpdateRequest;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomListResponse;
import org.springframework.data.domain.Page;

import java.util.List;


public interface BoardService {
    BoardResponse createBoard(BoardCreateRequest request, String userEmail);
    BoardResponse getBoardById(Long boardId, Long userId);
    List<BoardResponse> getAllBoards();
    void deleteBoard(Long boardId, Long userId);
    List<BoardResponse> getBoardsByUserId(Long userId);
    ChatRoomListResponse getBoardChatRooms(Long boardId);

    BoardResponse updateBoard(Long boardId, Long userId, BoardUpdateRequest request);

    Page<BoardNearbyDto> getNearbyBoards(double radiusKm, int page, int size);

}
