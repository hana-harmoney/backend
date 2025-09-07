package com.example.hanaharmonybackend.repository;

import com.example.hanaharmonybackend.domain.Board;
import com.example.hanaharmonybackend.domain.Category;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.domain.enumerate.GENDER;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * 네이티브 쿼리 findNearbyIdsWithDistanceAll(lat, lon, radiusKm)에 대한 통합 테스트.
 * - 반경 필터링이 정확한지
 * - 종료된 글(status=1) 제외가 되는지
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(com.example.hanaharmonybackend.config.TestJpaAuditingConfig.class)
class BoardRepositoryDistanceStatusTest {

    @Autowired BoardRepository boardRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired UserRepository userRepository;

    // 기준 좌표 (분당 근처)
    private static final double BASE_LAT = 37.3117774938582;
    private static final double BASE_LON = 127.074050507072;

    @Test
    void radius6km_then_includeOnlyActiveInsideRadius_excludeClosed() {
        double radiusKm = 6.0;

        User owner = userRepository.save(user(null));
        Category cat = categoryRepository.save(category(null, "일반"));

        // A: 반경 안 + 활성(status=false == 0)  → 포함
        Board activeNear = board(null, owner, cat, "활성-근처", 37.312, 127.075, false);

        // B: 반경 안 + 종료(status=true == 1)   → 제외
        Board closedNear = board(null, owner, cat, "종료-근처", 37.313, 127.076, true);

        // C: 반경 밖 + 활성                     → 제외
        Board activeFar = board(null, owner, cat, "활성-먼곳", 37.4979, 127.0276, false);

        boardRepository.saveAll(List.of(activeNear, closedNear, activeFar));

        // when
        List<Object[]> rows = boardRepository.findNearbyIdsWithDistanceAll(BASE_LAT, BASE_LON, radiusKm);

        // then: A만 반환되어야 함
        assertThat(rows).hasSize(1);
        Long onlyId = ((Number) rows.get(0)[0]).longValue();
        Double onlyDist = ((Number) rows.get(0)[1]).doubleValue();

        assertThat(onlyId).isEqualTo(activeNear.getBoardId());
        assertThat(onlyDist).isLessThan(radiusKm);
    }

    @Test
    void largeRadius_then_includeAllActive_only_sortedByDistanceAsc() {
        double radiusKm = 60.0;

        User owner = userRepository.save(user(null));
        Category cat = categoryRepository.save(category(null, "일반"));

        Board activeNear = board(null, owner, cat, "활성-근처", 37.312, 127.075, false);          // 반경 안
        Board closedNear = board(null, owner, cat, "종료-근처", 37.313, 127.076, true);            // 반경 안(종료)
        Board activeFar = board(null, owner, cat, "활성-먼곳", 37.4979, 127.0276, false);          // 반경 안(멀리, 강남)

        boardRepository.saveAll(List.of(activeNear, closedNear, activeFar));

        // when
        List<Object[]> rows = boardRepository.findNearbyIdsWithDistanceAll(BASE_LAT, BASE_LON, radiusKm);

        // then: 활성만 2건, 거리 오름차순이어야 함 (near → far)
        assertThat(rows).hasSize(2);

        long firstId = ((Number) rows.get(0)[0]).longValue();
        long secondId = ((Number) rows.get(1)[0]).longValue();
        double firstDist = ((Number) rows.get(0)[1]).doubleValue();
        double secondDist = ((Number) rows.get(1)[1]).doubleValue();

        assertThat(firstId).isEqualTo(activeNear.getBoardId());
        assertThat(secondId).isEqualTo(activeFar.getBoardId());
        assertThat(firstDist).isLessThan(secondDist);
    }

    // ---------- helpers ----------

    private User user(Long id) {
        User u = new User();
        u.setId(id);
        u.setLoginId("user_" + System.nanoTime());  // unique 보장용
        u.setName("tester");
        u.setPassword("user123@");
        u.setPhone("010-1111-2222");
        u.setBirth("20021101");
        u.setAddress("서울 동작구 강남초등길 2");
        u.setGender(GENDER.FEMALE);

        // 위치값 (테스트 시드)
        u.setLatitude(37.31);
        u.setLongitude(127.07);
        // createdAt/updatedAt 은 Auditing 으로 자동 세팅됨 (setter 호출 X)
        return u;
    }

    private Category category(Long id, String name) {
        Category c = new Category();
        c.setCategoryId(id);
        c.setName(name);
        return c;
    }

    private Board board(Long id, User owner, Category category,
                        String title, double lat, double lon, boolean status) {
        Board b = new Board();
        b.setBoardId(id);
        b.setUser(owner);
        b.setCategory(category);
        b.setTitle(title);
        b.setContent("content");
        b.setWage(10000L);
        b.setAddress("addr");
        b.setLatitude(lat);
        b.setLongitude(lon);
        b.setImageUrl("img");
        b.setStatus(status); // true=1(종료), false=0(활성)
        b.setCreatedAt(LocalDateTime.now().minusHours(3));
        b.setUpdatedAt(LocalDateTime.now());
        return b;
    }

    /** ✅ @DataJpaTest 슬라이스에서 Querydsl 빈(JPAQueryFactory) 제공 */
    @TestConfiguration
    static class QuerydslTestConfig{
        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager em){
            return new JPAQueryFactory(em);
        }
    }
}