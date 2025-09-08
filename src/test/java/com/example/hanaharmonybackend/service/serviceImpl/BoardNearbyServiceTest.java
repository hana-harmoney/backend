package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.Board;
import com.example.hanaharmonybackend.domain.Category;
import com.example.hanaharmonybackend.domain.Profile;
import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.BoardRepository;
import com.example.hanaharmonybackend.repository.CategoryRepository;
import com.example.hanaharmonybackend.repository.ChatRoomRepository;
import com.example.hanaharmonybackend.util.SecurityUtil;
import com.example.hanaharmonybackend.web.dto.board.BoardNearbyDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * getNearbyBoards(radius) 서비스 로직 검증:
 * - 레포 쿼리에서 받은 (id, distance) 순서를 그대로 유지해 DTO 구성하는지
 * - (이 테스트에서는) chatRoom 관련 필드는 검증하지 않음
 * - 사용자 위치 없을 때 예외 처리하는지
 */
@ExtendWith(MockitoExtension.class)
class BoardNearbyServiceTest {

    @Mock BoardRepository boardRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock ChatRoomRepository chatRoomRepository;

    @InjectMocks
    BoardServiceImpl service;

    final double RADIUS = 6.0;
    final double ME_LAT = 37.3117774938582;
    final double ME_LON = 127.074050507072;

    User me;
    Board b1; // 가까운 활성
    Board b2; // 조금 먼 활성

    @BeforeEach
    void setUp() {
        me = user(100L, "010-1234-5678", ME_LAT, ME_LON, profile("나프로필", 4.5, "p/me.jpg"));
        b1 = board(1L, me, "가까움", 37.312, 127.075, false);
        b2 = board(2L, user(200L, "010-0000-0000", 37.4979, 127.0276, profile("상대", 3.2, "p/other.jpg")),
                "조금-먼", 37.4979, 127.0276, false);
    }

    @Test
    void getNearbyBoards_keepsOrderAndDistanceMapping_only() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentMember).thenReturn(me);

            // 레포가 거리 오름차순으로 id, distance를 내려준다고 가정
            when(boardRepository.findNearbyIdsWithDistanceAll(ME_LAT, ME_LON, RADIUS))
                    .thenReturn(List.of(new Object[]{1L, 0.3}, new Object[]{2L, 8.7}));

            // findAllById의 반환 순서를 일부러 섞어도 rows 순서를 유지해야 함
            when(boardRepository.findAllById(List.of(1L, 2L)))
                    .thenReturn(List.of(b2, b1));

            List<BoardNearbyDto> result = service.getNearbyBoards(RADIUS);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getBoardId()).isEqualTo(1L);
            assertThat(result.get(0).getDistance()).isEqualTo(0.3);
            assertThat(result.get(1).getBoardId()).isEqualTo(2L);
            assertThat(result.get(1).getDistance()).isEqualTo(8.7);

            // 필요 상호작용 검증
            verify(boardRepository).findNearbyIdsWithDistanceAll(ME_LAT, ME_LON, RADIUS);
            verify(boardRepository).findAllById(List.of(1L, 2L));
        }
    }

    @Test
    void getNearbyBoards_throwIfNoUserLocation() {
        User noLoc = user(999L, "010-0000-0000", null, null, profile("no", 0.0, null));
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentMember).thenReturn(noLoc);

            assertThatThrownBy(() -> service.getNearbyBoards(RADIUS))
                    .isInstanceOf(CustomException.class);
        }
    }

    // ----- helpers -----
    private User user(Long id, String phone, Double lat, Double lon, Profile profile) {
        User u = new User();
        u.setId(id);
        u.setPhone(phone);
        u.setLatitude(lat);
        u.setLongitude(lon);
        u.setProfile(profile);
        return u;
    }

    private Profile profile(String nickname, Double trust, String img) {
        Profile p = new Profile();
        p.setNickname(nickname);
        p.setTrust(trust);
        p.setProfileImg(img);
        return p;
    }

    private Board board(Long id, User owner, String title, double lat, double lon, boolean status) {
        Category cat = new Category();
        cat.setCategoryId(1L);
        cat.setName("일반");

        Board b = new Board();
        b.setBoardId(id);
        b.setUser(owner);
        b.setCategory(cat);
        b.setTitle(title);
        b.setContent("content");
        b.setWage(10000L);
        b.setAddress("addr");
        b.setLatitude(lat);
        b.setLongitude(lon);
        b.setImageUrl("img");
        b.setStatus(status);
        b.setCreatedAt(LocalDateTime.now().minusHours(3));
        b.setUpdatedAt(LocalDateTime.now());
        return b;
    }
}