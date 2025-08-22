package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.domain.Board;
import com.example.hanaharmonybackend.domain.Category;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.repository.BoardRepository;
import com.example.hanaharmonybackend.repository.CategoryRepository;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.serviceImpl.BoardService;
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

    @Override
    public BoardResponse createBoard(BoardCreateRequest request, String userEmail) {
        User user = userRepository.findByLoginId(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        Profile profile=user.getProfile();

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .wage(request.getWage())
                .address(request.getAddress())
                .imageUrl(request.getImageUrl())
                .user(user)
                .category(category)
                .build();

        Board saved = boardRepository.save(board);

        return BoardResponse.builder()
                .boardId(saved.getBoardId())
                .nickname(profile.getNickname())
                .trust((int) profile.getTrust())
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
}