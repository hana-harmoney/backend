package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.ChatMessage;
import com.example.hanaharmonybackend.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<ChatMessage> findTopByRoomOrderByCreatedAtDesc(ChatRoom room);
    List<ChatMessage> findByRoomOrderByCreatedAtAsc(ChatRoom room);
}