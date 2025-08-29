package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE c.user1.id = :userId OR c.user2.id = :userId")
    List<ChatRoom> findByUserId(@Param("userId") Long userId);

    Long countByBoard_BoardId(Long boardId);

    @Query("SELECT c.id FROM ChatRoom c WHERE c.board.boardId = :boardId AND c.user2.id = :userId")
    Optional<Long> findIdByBoardIdAndUser2Id(@Param("boardId") Long boardId,
                                             @Param("userId") Long userId);

    List<ChatRoom> findByBoard_BoardId(Long boardId);
}