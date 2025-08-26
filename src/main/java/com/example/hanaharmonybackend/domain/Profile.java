package com.example.hanaharmonybackend.domain;

import com.example.hanaharmonybackend.domain.DescImage;
import com.example.hanaharmonybackend.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "profiles")
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "description", length = 3000)
    private String description;

    @Column(name = "profile_img", length = 255)
    private String profileImg;

    @Column(name = "category_ids", columnDefinition = "json")
    private String categoryIdsJson;

    @Column(name = "trust", nullable = false) @ColumnDefault("5")
    private Double trust;

    @Column(name = "match_count", nullable = false) @ColumnDefault("0")
    private Integer matchCount;

    @Column(name = "report_count", nullable = false) @ColumnDefault("0")
    private Integer reportCount;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DescImage> images = new ArrayList<>();

    /** created_at / updated_at 없음 → 시간 콜백 제거
     *  (DB DEFAULT 사용 시 trust/match/report 기본값은 DB가 채움)
     */
    @PrePersist
    void prePersist() {
        if (trust == null) trust = 5d;
        if (matchCount == null) matchCount = 0;
        if (reportCount == null) reportCount = 0;
    }

    public void increaseReportCount() {
        this.reportCount = this.reportCount + 1;
    }
}
