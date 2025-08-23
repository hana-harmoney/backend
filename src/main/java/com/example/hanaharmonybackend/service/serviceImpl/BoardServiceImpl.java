package com.example.hanaharmonybackend.service.serviceImpl;


import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;

import com.example.hanaharmonybackend.domain.Board;
import com.example.hanaharmonybackend.domain.Category;
import com.example.hanaharmonybackend.domain.Profile;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.repository.BoardRepository;
import com.example.hanaharmonybackend.repository.CategoryRepository;
import com.example.hanaharmonybackend.repository.ProfileRepository;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.BoardService;
import com.example.hanaharmonybackend.web.dto.BoardCreateRequest;
import com.example.hanaharmonybackend.web.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Override
    public BoardResponse createBoard(BoardCreateRequest request, String userEmail) {
        User user = userRepository.findByLoginId(userEmail)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorStatus.CATEGORY_NOT_FOUND));
        Profile profile = profileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new CustomException(ErrorStatus.PROFILE_NOT_FOUND));

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .wage(request.getWage())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .imageUrl(request.getImageUrl())
                .user(user)
                .category(category)
                .status(false)
                .build();

        Board saved = boardRepository.save(board);

        return BoardResponse.builder()
                .boardId(saved.getBoardId())
                .nickname(profile.getNickname())
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
                .build();
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
    @Transactional(readOnly = true)
    public BoardResponse getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorStatus.BOARD_NOT_FOUND));
        return toResponse(board);
    }

    private BoardResponse toResponse(Board board) {
        return BoardResponse.builder()
                .boardId(board.getBoardId())
                .nickname(board.getUser().getProfile().getNickname())
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
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponse> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponse> getBoardsByUserId(Long userId) {
        List<Board> boards = boardRepository.findByUser_Id(userId);
        return boards.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}