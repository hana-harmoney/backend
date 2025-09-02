package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByUser_Id(Long userId);         // 특정 userId에 프로필이 이미 있는지 체크
    Optional<Profile> findByUser_Id(Long userId); // userId로 프로필 조회
    boolean existsByUserId(Long userId);
}
