package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.web.dto.BoardCreateRequest;
import com.example.hanaharmonybackend.web.dto.BoardResponse;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomListResponse;

import java.util.List;


public interface BoardService {
    BoardResponse createBoard(BoardCreateRequest request, String userEmail);
    BoardResponse getBoardById(Long boardId, Long userId);
    List<BoardResponse> getAllBoards();
    void deleteBoard(Long boardId, Long userId);
    List<BoardResponse> getBoardsByUserId(Long userId);
    ChatRoomListResponse getBoardChatRooms(Long boardId);
}
