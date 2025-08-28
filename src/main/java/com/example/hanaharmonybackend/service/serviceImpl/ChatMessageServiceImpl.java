package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.*;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.*;
import com.example.hanaharmonybackend.service.ChatMessageService;
import com.example.hanaharmonybackend.service.ChatRoomService;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.chatMessage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionHistoryRepository txRepository;
    private final ChatRoomService chatRoomService;

    // 메세지 보내기
    @Override
    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, String loginId) {
        User sender = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));

        User receiver = room.getUser1().getId().equals(sender.getId())
                ? room.getUser2()
                : room.getUser1();

        ChatMessage chatMessage = new ChatMessage(
                request.getMessage(),
                request.getAmount(),
                room,
                sender,
                receiver
        );

        ChatMessage saved = chatMessageRepository.save(chatMessage);
        return ChatMessageResponse.from(saved);
    }

    // 특정 채팅방의 메세지 리스트 조회
    @Override
    public ChatMessageListResponse getMessagesByRoomId(Long roomId) {
        User loginUser = SecurityUtil.getCurrentMember();
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));
        if (!chatRoomService.isMember(roomId, loginUser.getLoginId())) {
            throw new CustomException(ErrorStatus.CHATROOM_ACCESS_DENIED);
        }

        List<ChatMessage> messages = chatMessageRepository.findAllByRoomIdOrderByCreatedAtAsc(roomId);

        List<ChatMessageResponse> messageList = messages.stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());

        return ChatMessageListResponse.builder()
                .chatMessageList(messageList)
                .build();
    }

    // 채팅방 송금
    @Override
    public ChatMessageTransferResponse chatTransferAccountToAccount(Long roomId, ChatMessageTransferRequest request) {
        User loginUser = SecurityUtil.getCurrentMember();

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));

        Long amount = request.getAmount();

        if (!chatRoomService.isMember(roomId, loginUser.getLoginId())) {
            throw new CustomException(ErrorStatus.CHATROOM_ACCESS_DENIED);
        }
        if (room.getIsReceived()) {
            throw new CustomException(ErrorStatus.TRANSFER_ALREADY_COMPLETED);
        }
        if (amount == null || amount <= 0) {
            throw new CustomException(ErrorStatus.INVALID_TRANSFER_AMOUNT);
        }

        // 송금은 항상 user1 -> user2
        User fromUser = room.getUser1();
        User toUser = room.getUser2();

        if (!loginUser.getId().equals(fromUser.getId())) {
            throw new CustomException(ErrorStatus.CHATROOM_TRANSFER_DENIED);
        }

        Account from = accountRepository.findByUser_Id(fromUser.getId())
                .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));
        Account to = accountRepository.findByUser_Id(toUser.getId())
                .orElseThrow(() -> new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND));
        Board board = room.getBoard();

        // 송금하기
        from.withdraw(amount);
        to.deposit(amount);

        // 거래 내역 생성
        txRepository.save(TransactionHistory.builder()
                .fromAccount(from)
                .toAccount(to)
                .amount(amount)
                .build());

        // 송금 후 게시글 상태, 채팅방 송금 여부, 사용자 매칭횟수 증가
        board.updateStatus(true);
        room.updateIsReceived(true);
        fromUser.getProfile().increaseMatchCount();
        toUser.getProfile().increaseMatchCount();

        // 송금 완료 메시지 생성
        ChatMessage transferMessage = new ChatMessage(
                "[" + fromUser.getProfile().getNickname() + "] 님이 " + amount + "원을 송금하셨습니다.",
                amount,
                room,
                fromUser,
                toUser
        );

        ChatMessage savedMessage = chatMessageRepository.save(transferMessage);

        return ChatMessageTransferResponse.builder()
                .change(from.getAccountBalance())   // 잔액
                .toAccountNum(to.getAccountNum())   // 받은 사람 계좌
                .toAccountName(to.getUser().getName())  // 받은 사람 이름
                .toAccountNickname(to.getUser().getProfile().getNickname()) // 받은 사람 닉네임
                .amount(amount) // 보낸 금액
                .chatMessage(ChatMessageResponse.from(savedMessage)) // 채팅 메세지
                .build();
    }
}