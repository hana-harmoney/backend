package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.domain.Board;
import com.example.hanaharmonybackend.web.dto.BoardCreateRequest;
import com.example.hanaharmonybackend.web.dto.BoardResponse;


public interface BoardService {
    BoardResponse createBoard(BoardCreateRequest request, String userEmail);
}
