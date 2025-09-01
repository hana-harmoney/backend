package com.example.hanaharmonybackend.repository;
import com.example.hanaharmonybackend.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByUser_Id(Long userId, Sort createdAt);

    //거리 기반 구직 글 필터링(15km)
    @Query(value = """
        SELECT 
            b.board_id    AS id,
            (6371 * ACOS(
                COS(RADIANS(:lat)) * COS(RADIANS(b.latitude)) *
                COS(RADIANS(b.longitude) - RADIANS(:lon)) +
                SIN(RADIANS(:lat)) * SIN(RADIANS(b.latitude))
            ))             AS distance_km
        FROM board b
        WHERE b.latitude IS NOT NULL
          AND b.longitude IS NOT NULL
        HAVING distance_km <= :radiusKm
        ORDER BY distance_km ASC
        """,
            countQuery = """
        SELECT COUNT(*) 
        FROM (
            SELECT 1
            FROM board b
            WHERE b.latitude IS NOT NULL
              AND b.longitude IS NOT NULL
              AND (6371 * ACOS(
                    COS(RADIANS(:lat)) * COS(RADIANS(b.latitude)) *
                    COS(RADIANS(b.longitude) - RADIANS(:lon)) +
                    SIN(RADIANS(:lat)) * SIN(RADIANS(b.latitude))
                  )) <= :radiusKm
        ) x
        """,
            nativeQuery = true)
    Page<Object[]> findNearbyIdsWithDistance(@Param("lat") double lat,
                                             @Param("lon") double lon,
                                             @Param("radiusKm") double radiusKm,
                                             Pageable pageable);
}