package com.example.hanaharmonybackend.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "desc_images")
public class DescImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "desc_img_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "img_url", nullable = false, length = 300)
    private String imgUrl;
}
