package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}