package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Board;
import com.example.hanaharmonybackend.domain.ChatRoom;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.BoardRepository;
import com.example.hanaharmonybackend.repository.ChatRoomRepository;
import com.example.hanaharmonybackend.service.ChatRoomService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.ChatRoomRequest;
import com.example.hanaharmonybackend.web.dto.ChatRoomCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final BoardRepository boardRepository;

    public ChatRoomCreateResponse createChatRoom(ChatRoomRequest request) {
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorStatus.BOARD_NOT_FOUND));
        User boardWriter = board.getUser();
        User loginUser = SecurityUtil.getCurrentMember();

        ChatRoom chatRoom = new ChatRoom(boardWriter, loginUser, board, false);
        return ChatRoomCreateResponse.fromEntity(chatRoomRepository.save(chatRoom));
    }
}
