package com.example.hanaharmonybackend.web.controller;
import com.example.hanaharmonybackend.web.dto.board.BoardUpdateRequest;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomListResponse;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.ApiResponse;
import com.example.hanaharmonybackend.service.BoardService;
import com.example.hanaharmonybackend.web.dto.board.BoardCreateRequest;
import com.example.hanaharmonybackend.web.dto.board.BoardResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.hanaharmonybackend.util.SecurityUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //일자리 등록
    @Operation(summary = "일자리 등록")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> createBoard(@ModelAttribute BoardCreateRequest request) {
        User user = SecurityUtil.getCurrentMember();
        String loginId = user.getLoginId();
        BoardResponse response = boardService.createBoard(request, loginId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "일자리 글 수정")
    @PatchMapping(value = "/{boardId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> updateBoard(
            @PathVariable Long boardId,
            @ModelAttribute BoardUpdateRequest request
    ) {
        User user = SecurityUtil.getCurrentMember();
        Long userId = user.getId();
        BoardResponse response = boardService.updateBoard(boardId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //단건조회
    @Operation(summary = "일자리 단건 조회")
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<?>> getBoardById(@PathVariable Long boardId) {
        User user = SecurityUtil.getCurrentMember();
        Long userId = user.getId();
        BoardResponse response = boardService.getBoardById(boardId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //전체 조회
    @Operation(summary = "일자리 전체 리스트 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllBoards() {
        List<BoardResponse> response = boardService.getAllBoards();
        return ResponseEntity.ok(ApiResponse.success(java.util.Map.of("boardList", response)));
    }

    @Operation(summary = "사용자 주변(기본 6km) 게시글 리스트 조회")
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<?>> getNearbyBoards(
            @RequestParam(required = false) Double radiusKm
    ) {
        double radius = (radiusKm == null ? 6.0 : radiusKm);
        var result = boardService.getNearbyBoards(radius);
        return ResponseEntity.ok(ApiResponse.success(java.util.Map.of("boardList", result)));
    }

    //일자리 삭제
    @Operation(summary = "일자리 삭제")
    @DeleteMapping("/{boardId}")
    public ApiResponse<?> deleteBoard(@PathVariable Long boardId){
        User user=SecurityUtil.getCurrentMember();
        boardService.deleteBoard(boardId, user.getId());
        return ApiResponse.success("게시글 삭제가 완료되었습니다.");
    }

    //내 글조회
    @Operation(summary = "내가 작성한 일자리 리스트 조회")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<?>> getMyBoards() {
        User user = SecurityUtil.getCurrentMember();
        List<BoardResponse> boardList = boardService.getBoardsByUserId(user.getId());
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("boardList", boardList);
        return ResponseEntity.ok().body(ApiResponse.success(result));
    }

    @Operation(summary = "개설된 채팅방 리스트 조회")
    @GetMapping("/{boardId}/chatRoom")
    public ApiResponse<ChatRoomListResponse> getBoardChatRooms(@PathVariable Long boardId) {
        return ApiResponse.success(boardService.getBoardChatRooms(boardId));
    }


}
