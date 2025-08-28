package com.example.hanaharmonybackend.repository;
import com.example.hanaharmonybackend.domain.Board;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByUser_Id(Long userId, Sort createdAt);
}