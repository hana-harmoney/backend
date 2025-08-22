package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Board;
import com.example.hanaharmonybackend.domain.ChatMessage;
import com.example.hanaharmonybackend.domain.ChatRoom;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.BoardRepository;
import com.example.hanaharmonybackend.repository.ChatMessageRepository;
import com.example.hanaharmonybackend.repository.ChatRoomRepository;
import com.example.hanaharmonybackend.service.ChatRoomService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.ChatRoomInfoDto;
import com.example.hanaharmonybackend.web.dto.ChatRoomListResponse;
import com.example.hanaharmonybackend.web.dto.ChatRoomRequest;
import com.example.hanaharmonybackend.web.dto.ChatRoomCreateResponse;
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

    public ChatRoomCreateResponse createChatRoom(ChatRoomRequest request) {
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorStatus.BOARD_NOT_FOUND));
        User boardWriter = board.getUser();
        User loginUser = SecurityUtil.getCurrentMember();

        ChatRoom chatRoom = new ChatRoom(boardWriter, loginUser, board, false);
        return ChatRoomCreateResponse.fromEntity(chatRoomRepository.save(chatRoom));
    }

    @Override
    public ChatRoomListResponse getChatRoomList() {
        User loginUser = SecurityUtil.getCurrentMember();
        Long loginUserId = loginUser.getId();

        List<ChatRoomInfoDto> chatRoomList = chatRoomRepository.findByUserId(loginUserId)
                .stream()
                .map(chatRoom -> {
                    User otherUser = chatRoom.getUser1().getId().equals(loginUserId)
                            ? chatRoom.getUser2()
                            : chatRoom.getUser1();

                    ChatMessage lastMessage = chatMessageRepository
                            .findTopByRoomOrderByCreatedAtDesc(chatRoom)
                            .orElse(null);

                    return ChatRoomInfoDto.builder()
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
}
