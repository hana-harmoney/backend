package com.example.hanaharmonybackend.service.serviceImpl;


import com.example.hanaharmonybackend.domain.Board;
import com.example.hanaharmonybackend.domain.Category;
import com.example.hanaharmonybackend.domain.Profile;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.BoardRepository;
import com.example.hanaharmonybackend.repository.CategoryRepository;
import com.example.hanaharmonybackend.repository.ProfileRepository;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.BoardService;
import com.example.hanaharmonybackend.service.FileStorageService;
import com.example.hanaharmonybackend.web.dto.BoardCreateRequest;
import com.example.hanaharmonybackend.web.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final FileStorageService fileStorageService;

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
        return toResponse(board, isMine);
    }

    private BoardResponse toResponse(Board board, boolean isMine) {
        return BoardResponse.builder()
                .boardId(board.getBoardId())
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
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .isMine(isMine)
                .build();
    }

    @Override
    public List<BoardResponse> getAllBoards() {
        List<Board> boards = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return boards.stream()
                .map(board -> toResponse(board, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<BoardResponse> getBoardsByUserId(Long userId) {
        List<Board> boards = boardRepository.findByUser_Id(userId, Sort.by(Sort.Direction.DESC, "createdAt"));
        return boards.stream()
                .map(board -> toResponse(board, false))
                .collect(Collectors.toList());
    }
}