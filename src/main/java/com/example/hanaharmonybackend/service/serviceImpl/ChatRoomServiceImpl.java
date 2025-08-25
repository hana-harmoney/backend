package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.*;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.BoardRepository;
import com.example.hanaharmonybackend.repository.ChatMessageRepository;
import com.example.hanaharmonybackend.repository.ChatRoomRepository;
import com.example.hanaharmonybackend.service.ChatRoomService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.chatMessage.ChatMessageResponse;
import com.example.hanaharmonybackend.web.dto.chatRoom.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final BoardRepository boardRepository;

    // 채팅방 생성
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

        if (!room.getUser1().getId().equals(loginUser.getId()) &&
                !room.getUser2().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorStatus.CHATROOM_ACCESS_DENIED);
        }

        Board board = room.getBoard();
        User writer = board.getUser();
        Profile writerProfile = writer.getProfile();

        return ChatRoomDetailResponse.builder()
                .boardId(board.getBoardId())
                .writerId(writer.getId())
                .nickname(writerProfile.getNickname())
                .profileUrl(writerProfile.getProfileImg())
                .title(board.getTitle())
                .wage(board.getWage())
                .address(board.getAddress())
                .build();
    }

    @Override
    public List<ChatRoomInfoResponse> getChatRoomsForUser(Long userId) {
        return chatRoomRepository.findByUserId(userId)
                .stream()
                .map(chatRoom -> {
                    User otherUser = chatRoom.getUser1().getId().equals(userId)
                            ? chatRoom.getUser2()
                            : chatRoom.getUser1();

                    // 마지막 메시지 조회
                    var lastMessageOpt = chatMessageRepository.findTopByRoomOrderByCreatedAtDesc(chatRoom);

                    String lastMessage = lastMessageOpt.map(m -> m.getMessage()).orElse("메세지가 없습니다.");
                    var lastMessageTime = lastMessageOpt.map(m -> m.getCreatedAt()).orElse(null);

                    return new ChatRoomInfoResponse(
                            chatRoom.getId(),
                            otherUser.getProfile().getNickname(),
                            otherUser.getProfile().getProfileImg(),
                            lastMessageTime,
                            lastMessage
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageResponse> getMessagesForRoom(Long roomId) {
        var room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));

        List<ChatMessage> messages = chatMessageRepository.findByRoomOrderByCreatedAtAsc(room);

        return messages.stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());
    }
}
