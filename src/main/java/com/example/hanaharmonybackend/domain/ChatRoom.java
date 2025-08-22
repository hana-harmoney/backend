package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chatRoom")
@Getter
@NoArgsConstructor
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "is_received", nullable = false)
    private Boolean isReceived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id2", nullable = false)
    private User user2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    public ChatRoom(User user1, User user2, Board board, Boolean isReceived) {
        this.user1 = user1;
        this.user2 = user2;
        this.board = board;
        this.isReceived = isReceived;
    }
}