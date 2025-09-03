package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.board.BoardCreateRequest;
import com.example.hanaharmonybackend.web.dto.board.BoardNearbyDto;
import com.example.hanaharmonybackend.web.dto.board.BoardResponse;
import com.example.hanaharmonybackend.web.dto.board.BoardUpdateRequest;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomListResponse;

import java.util.List;


public interface BoardService {
    BoardResponse createBoard(BoardCreateRequest request, String userEmail);
    BoardResponse getBoardById(Long boardId, Long userId);
    List<BoardResponse> getAllBoards();
    void deleteBoard(Long boardId, Long userId);
    List<BoardResponse> getBoardsByUserId(Long userId);
    ChatRoomListResponse getBoardChatRooms(Long boardId);

    BoardResponse updateBoard(Long boardId, Long userId, BoardUpdateRequest request);

    List<BoardNearbyDto> getNearbyBoards(double radiusKm);

}
