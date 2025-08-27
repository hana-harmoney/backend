package com.example.hanaharmonybackend.web.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;


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

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //일자리 등록
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> createBoard(
            @RequestPart("request") String requestJson,
            @RequestPart("image") MultipartFile image) {
        ObjectMapper objectMapper = new ObjectMapper();
        BoardCreateRequest  request;
        try {
            request = objectMapper.readValue(requestJson, BoardCreateRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid request format", e);
        }

        User user = SecurityUtil.getCurrentMember();
        String loginId = user.getLoginId();
        request.setImage(image);
        BoardResponse response = boardService.createBoard(request, loginId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //단건조회
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<?>> getBoardById(@PathVariable Long boardId) {
        BoardResponse response = boardService.getBoardById(boardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllBoards() {
        List<BoardResponse> response = boardService.getAllBoards();
        return ResponseEntity.ok(ApiResponse.success(java.util.Map.of("boardList", response)));
    }

    //일자리 삭제
    @DeleteMapping("/{boardId}")
    public ApiResponse<?> deleteBoard(@PathVariable Long boardId){
        User user=SecurityUtil.getCurrentMember();
        boardService.deleteBoard(boardId, user.getId());
        return ApiResponse.success("게시글 삭제가 완료되었습니다.");
    }

    //내 글조회
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyBoards() {
        User user = SecurityUtil.getCurrentMember();
        List<BoardResponse> boards = boardService.getBoardsByUserId(user.getId());
        Map<String, Object> result = Map.of(
            "message", "내가 작성한 게시글 목록을 성공적으로 불러왔습니다.",
            "boardList", boards
        );
        return ResponseEntity.ok(result);
    }
}
