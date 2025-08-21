package com.example.hanaharmonybackend.domain;

import com.example.hanaharmonybackend.domain.enumerate.GENDER;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "ux_users_login_id", columnList = "login_id", unique = true)
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_id", nullable = false, length = 20, unique = true)
    private String loginId;

    // DB 스키마 컬럼명이 password 이므로 그대로 매핑
    @Column(name = "password", nullable = false, length = 255)
    private String password; // 해시 보관

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "birth", nullable = false, length = 10)
    private String birth; // yyyy-MM-dd 등 문자열 저장 스키마에 맞춤

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private GENDER gender;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;
}
