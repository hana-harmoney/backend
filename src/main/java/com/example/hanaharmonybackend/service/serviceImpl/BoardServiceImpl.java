package com.example.hanaharmonybackend.service.serviceImpl;


import com.example.hanaharmonybackend.domain.*;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.*;
import com.example.hanaharmonybackend.service.BoardService;
import com.example.hanaharmonybackend.service.FileStorageService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.BoardCreateRequest;
import com.example.hanaharmonybackend.web.dto.BoardNearbyDto;
import com.example.hanaharmonybackend.web.dto.BoardResponse;
import com.example.hanaharmonybackend.web.dto.BoardUpdateRequest;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomInfoResponse;
import com.example.hanaharmonybackend.web.dto.chatRoom.ChatRoomListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FileStorageService fileStorageService;
    private final ChatRoomServiceImpl chatRoomServiceImpl;

    @Override
    @Transactional
    public BoardResponse createBoard(BoardCreateRequest request, String userEmail) {
        User user = userRepository.findByLoginId(userEmail)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorStatus.CATEGORY_NOT_FOUND));
        Profile profile = profileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new CustomException(ErrorStatus.PROFILE_NOT_FOUND));

        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = fileStorageService.upload(request.getImage(), "upload/board");
        }

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .wage(request.getWage())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .imageUrl(imageUrl) //null 허용
                .user(user)
                .category(category)
                .status(false)
                .build();

        Board saved = boardRepository.save(board);

        return BoardResponse.builder()
                .boardId(saved.getBoardId())
                .userId(board.getUser().getId())
                .nickname(profile.getNickname())
                .phone(board.getUser().getPhone())
                .trust(profile.getTrust())
                .title(saved.getTitle())
                .content(saved.getContent())
                .wage(saved.getWage())
                .address(saved.getAddress())
                .latitude(saved.getLatitude())
                .longitude(saved.getLongitude())
                .imageUrl(saved.getImageUrl())
                .category(category.getName())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .profileUrl(board.getUser().getProfile().getProfileImg())
                .isMine(saved.getUser().getLoginId().equals(userEmail))
                .build();
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(Long boardId, Long userId, BoardUpdateRequest request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorStatus.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorStatus.UNAUTHORIZED);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorStatus.CATEGORY_NOT_FOUND));

        String imageUrl = board.getImageUrl();

        if (request.isDeleteImage()) {
            if (imageUrl != null) {
                fileStorageService.delete(imageUrl);
            }
            imageUrl = null;
        } else if (request.getImage() != null && !request.getImage().isEmpty()) {
            if (imageUrl != null) {
                fileStorageService.delete(imageUrl);
            }
            imageUrl = fileStorageService.upload(request.getImage(), "upload/board");
        }

        board.updateBoard(
                request.getTitle(),
                request.getContent(),
                request.getWage(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                imageUrl,
                category
        );

        return toResponse(board, true, null, null);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorStatus.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorStatus.UNAUTHORIZED);
        }

        boardRepository.delete(board);
    }

    @Override
    public BoardResponse getBoardById(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorStatus.BOARD_NOT_FOUND));

        boolean isMine = board.getUser().getId().equals(userId);
        Long chatRoomCnt = 0L;
        Long chatRoomId = null;

        if (isMine) {
            chatRoomCnt = chatRoomRepository.countByBoard_BoardId(boardId);
        } else {
            chatRoomId = chatRoomRepository.findIdByBoardIdAndUser2Id(boardId, userId)
                    .orElse(null);
        }
        return toResponse(board, isMine, chatRoomCnt, chatRoomId);
    }

    private BoardResponse toResponse(Board board, boolean isMine, Long chatRoomCnt, Long chatRoomId) {
        return BoardResponse.builder()
                .boardId(board.getBoardId())
                .userId(board.getUser().getId())
                .nickname(board.getUser().getProfile().getNickname())
                .phone(board.getUser().getPhone())
                .profileUrl(board.getUser().getProfile().getProfileImg())
                .trust(board.getUser().getProfile().getTrust())
                .title(board.getTitle())
                .content(board.getContent())
                .wage(board.getWage())
                .address(board.getAddress())
                .latitude(board.getLatitude())
                .longitude(board.getLongitude())
                .imageUrl(board.getImageUrl())
                .category(board.getCategory().getName())
                .status(board.getStatus())
                .chatRoomCnt(chatRoomCnt)
                .chatRoomId(chatRoomId)
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .isMine(isMine)
                .build();
    }

    @Override
    public List<BoardResponse> getAllBoards() {
        List<Board> boards = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return boards.stream()
                .map(board -> toResponse(board, false, null, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<BoardResponse> getBoardsByUserId(Long userId) {
        List<Board> boards = boardRepository.findByUser_Id(userId, Sort.by(Sort.Direction.DESC, "createdAt"));
        return boards.stream()
                .map(board -> toResponse(board, false, null, null))
                .collect(Collectors.toList());
    }

    // 일자리에 개설된 채팅방리스트
    @Override
    public ChatRoomListResponse getBoardChatRooms(Long boardId) {
        User user = SecurityUtil.getCurrentMember();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorStatus.BOARD_NOT_FOUND));

        if (!user.getId().equals(board.getUser().getId())) {
            throw new CustomException(ErrorStatus.BOARD_NOT_WRITER);
        }

        List<ChatRoom> rooms = chatRoomRepository.findByBoard_BoardId(boardId);

        List<ChatRoomInfoResponse> chatRoomList = chatRoomServiceImpl.mapChatRooms(rooms, user.getId());

        return ChatRoomListResponse.builder()
                .chatRoomList(chatRoomList)
                .build();
    }


    // 사용자 위치 기준 반경 내 게시글 조회 (distance 포함)
    @Transactional(readOnly = true)
    public List<BoardNearbyDto> getNearbyBoards(double radius) {
        User me = SecurityUtil.getCurrentMember();
        if (me.getLatitude() == null || me.getLongitude() == null) {
            throw new CustomException(ErrorStatus.USER_LOCATION_REQUIRED);
        }

        // 1) 반경 내 전체 id + distance 조회 (정렬: 거리 ASC)
        List<Object[]> rows = boardRepository.findNearbyIdsWithDistanceAll(
                me.getLatitude(), me.getLongitude(), radius
        );

        // 2) id → distance 매핑 및 순서 보존
        List<Long> ids = rows.stream().map(r -> ((Number) r[0]).longValue()).toList();
        var distanceMap = new java.util.HashMap<Long, Double>();
        rows.forEach(r -> distanceMap.put(((Number) r[0]).longValue(), ((Number) r[1]).doubleValue()));

        // 3) 엔티티 배치 조회 후, rows 순서대로 DTO 조립
        var boardsById = boardRepository.findAllById(ids)
                .stream().collect(Collectors.toMap(Board::getBoardId, b -> b));

        var result = new java.util.ArrayList<BoardNearbyDto>(ids.size());
        for (Long boardId : ids) {
            Board b = boardsById.get(boardId);
            if (b == null) continue;

            boolean isMine = b.getUser().getId().equals(me.getId());
            Long chatRoomCnt = null;
            Long chatRoomId = null;
            if (isMine) {
                chatRoomCnt = chatRoomRepository.countByBoard_BoardId(boardId);
            } else {
                chatRoomId = chatRoomRepository.findIdByBoardIdAndUser2Id(boardId, me.getId()).orElse(null);
            }

            Profile profile = b.getUser().getProfile();

            result.add(
                    BoardNearbyDto.builder()
                            .boardId(b.getBoardId())
                            .userId(b.getUser().getId())
                            .nickname(profile.getNickname())
                            .phone(b.getUser().getPhone())
                            .trust(profile.getTrust())
                            .title(b.getTitle())
                            .content(b.getContent())
                            .wage(b.getWage())
                            .address(b.getAddress())
                            .latitude(b.getLatitude())
                            .longitude(b.getLongitude())
                            .imageUrl(b.getImageUrl())
                            .category(b.getCategory().getName())
                            .status(b.getStatus())
                            .profileUrl(profile.getProfileImg())
                            .isMine(isMine)
                            .chatRoomCnt(chatRoomCnt)
                            .chatRoomId(chatRoomId)
                            .createdAt(b.getCreatedAt())
                            .updatedAt(b.getUpdatedAt())
                            .distance(distanceMap.getOrDefault(boardId, 0.0))
                            .build()
            );
        }

        return result;
    }

}
