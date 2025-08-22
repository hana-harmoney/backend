package com.example.hanaharmonybackend.service.serviceImpl;


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
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        Profile profile = profileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .wage(request.getWage())
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
                .imageUrl(saved.getImageUrl())
                .category(category.getName())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponse getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
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
                .imageUrl(board.getImageUrl())
                .category(board.getCategory().getName())
                .status(board.getStatus())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}