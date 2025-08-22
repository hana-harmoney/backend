package com.example.hanaharmonybackend.repository;
import com.example.hanaharmonybackend.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;



public interface BoardRepository extends JpaRepository<Board, Long> {
}