package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.*;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.BoardRepository;
import com.example.hanaharmonybackend.repository.ChatMessageRepository;
import com.example.hanaharmonybackend.repository.ChatRoomRepository;
import com.example.hanaharmonybackend.repository.ProfileRepository;
import com.example.hanaharmonybackend.service.ChatRoomService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.chatRoom.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final BoardRepository boardRepository;
    private final ProfileRepository profileRepository;

    // 채팅방 생성
    @Override
    @Transactional
    public ChatRoomCreateResponse createChatRoom(ChatRoomRequest request) {
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorStatus.BOARD_NOT_FOUND));
        User boardWriter = board.getUser();
        User loginUser = SecurityUtil.getCurrentMember();

        ChatRoom chatRoom = new ChatRoom(boardWriter, loginUser, board, false);
        return ChatRoomCreateResponse.fromEntity(chatRoomRepository.save(chatRoom));
    }

    // 채팅방 목록 조회
    @Override
    public ChatRoomListResponse getChatRoomList() {
        User loginUser = SecurityUtil.getCurrentMember();
        Long loginUserId = loginUser.getId();

        List<ChatRoomInfoResponse> chatRoomList = chatRoomRepository.findByUserId(loginUserId)
                .stream()
                .map(chatRoom -> {
                    User otherUser = chatRoom.getUser1().getId().equals(loginUserId)
                            ? chatRoom.getUser2()
                            : chatRoom.getUser1();

                    ChatMessage lastMessage = chatMessageRepository
                            .findTopByRoomOrderByCreatedAtDesc(chatRoom)
                            .orElse(null);

                    return ChatRoomInfoResponse.builder()
                            .roomId(chatRoom.getId())
                            .nickname(otherUser.getProfile().getNickname())
                            .profileImageUrl(otherUser.getProfile().getProfileImg())
                            .lastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null)
                            .lastMessage(lastMessage != null ? lastMessage.getMessage() : "메세지가 없습니다.")
                            .build();
                })
                .collect(Collectors.toList());

        return ChatRoomListResponse.builder()
                .chatRoomList(chatRoomList)
                .build();
    }

    // 채팅방 단건 조회
    @Override
    public ChatRoomDetailResponse getChatRoomDetail(Long roomId) {
        User loginUser = SecurityUtil.getCurrentMember();
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));
        if (!isMember(roomId, loginUser.getLoginId())) {
            throw new CustomException(ErrorStatus.CHATROOM_ACCESS_DENIED);
        }

        User otherUser = room.getUser1().getId().equals(loginUser.getId())
                ? room.getUser2()
                : room.getUser1();

        Board board = room.getBoard();
        User writer = board.getUser();
        Profile otherUserProfile = otherUser.getProfile();

        return ChatRoomDetailResponse.builder()
                .boardId(board.getBoardId()) // 게시글 ID
                .writerId(writer.getId()) // 게시글 작성자 ID
                .name(otherUser.getName()) // 채팅 상대 이름
                .nickname(otherUserProfile.getNickname()) // 채팅 상대 닉네임
                .profileUrl(otherUserProfile.getProfileImg()) // 채팅 상대 프로필 사진
                .title(board.getTitle()) // 게시글 제목
                .wage(board.getWage()) // 게시글 시급
                .address(board.getAddress()) // 게시글 주소
                .isReceived(room.getIsReceived()) // 송금 여부
                .build();
    }

    // 채팅 상대 신고
    @Override
    @Transactional
    public ChatRoomReportResponse reportChatRoom(Long roomId) {
        User loginUser = SecurityUtil.getCurrentMember();
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));
        if (!isMember(roomId, loginUser.getLoginId())) {
            throw new CustomException(ErrorStatus.CHATROOM_ACCESS_DENIED);
        }

        User reportedUser = room.getUser1().getId().equals(loginUser.getId())
                ? room.getUser2()
                : room.getUser1();

        Profile reportedProfile = reportedUser.getProfile();
        reportedProfile.increaseReportCount();
        reportedProfile.updateTrust(-1.0);
        profileRepository.save(reportedProfile);

        return ChatRoomReportResponse.builder()
                .reportedUserId(reportedUser.getId())
                .reportCount(reportedProfile.getReportCount())
                .build();
    }

    // 채팅 거래 후기
    @Override
    @Transactional
    public ChatRoomReviewResponse reviewChatRoom(Long roomId, ChatRoomReviewRequest request) {
        User loginUser = SecurityUtil.getCurrentMember();
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));
        if (!isMember(roomId, loginUser.getLoginId())) {
            throw new CustomException(ErrorStatus.CHATROOM_ACCESS_DENIED);
        }

        Double score = request.getScore();
        if (score == null || !(score == -0.5 || score == 0.5 || score == 1.0)) {
            throw new CustomException(ErrorStatus.INVALID_REVIEW_SCORE);
        }

        User reviewedUser = room.getUser1().getId().equals(loginUser.getId())
                ? room.getUser2()
                : room.getUser1();

        Profile reviewedProfile = reviewedUser.getProfile();
        reviewedProfile.updateTrust(request.getScore());
        profileRepository.save(reviewedProfile);

        return ChatRoomReviewResponse.builder()
                .reviewedUserId(reviewedUser.getId())
                .score(request.getScore())
                .trust(reviewedProfile.getTrust())
                .build();
    }

    public boolean isMember(Long roomId, String loginId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));

        return room.getUser1().getLoginId().equals(loginId) ||
                room.getUser2().getLoginId().equals(loginId);
    }
}