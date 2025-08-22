package com.example.hanaharmonybackend.web.controller;


import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.BoardService;
import com.example.hanaharmonybackend.web.dto.BoardCreateRequest;
import com.example.hanaharmonybackend.web.dto.BoardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.hanaharmonybackend.util.SecurityUtil;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createBoard(@RequestBody @Valid BoardCreateRequest request) {
        User user=SecurityUtil.getCurrentMember();
        String loginId = user.getLoginId();

        BoardResponse response = boardService.createBoard(request, loginId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponse> getBoardById(@PathVariable Long boardId) {
        return ApiResponse.success(boardService.getBoardById(boardId));
    }
}
