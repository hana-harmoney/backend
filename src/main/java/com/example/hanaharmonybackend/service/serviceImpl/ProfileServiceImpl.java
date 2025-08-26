package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.domain.DescImage;
import com.example.hanaharmonybackend.domain.Profile;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.DescImageRepository;
import com.example.hanaharmonybackend.repository.ProfileRepository;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.FileStorageService;
import com.example.hanaharmonybackend.service.ProfileService;
import com.example.hanaharmonybackend.web.dto.ProfileCreateRequest;
import com.example.hanaharmonybackend.web.dto.ProfilePatchRequest;
import com.example.hanaharmonybackend.web.dto.ProfileResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Objects;


import static com.example.hanaharmonybackend.payload.code.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorage;
    private final DescImageRepository descImageRepository;
    private final ObjectMapper om = new ObjectMapper();
    private final PasswordEncoder passwordEncoder;
    /**
     * 프로필 등록 (JSON 방식)
     * - category_ids: List<Long> (숫자 배열) → JSON으로 저장
     * - img_url: String URL 리스트 (DescImage로 저장)
     */
    @Override
    public ProfileResponse create(Long currentUserId, ProfileCreateRequest req) {
        if (profileRepository.existsByUser_Id(currentUserId)) {
            throw new IllegalArgumentException("이미 프로필이 등록되어 있습니다.");
        }
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Profile p = Profile.builder()
                .user(user)
                .nickname(req.nickname())
                .description(req.description())
                .profileImg(req.profile_img())
                .categoryIds(writeJsonLong(req.category_ids()))
                .build();

        if (req.img_url() != null) {
            for (String url : req.img_url()) {
                if (url == null || url.isBlank()) continue;
                p.getImages().add(DescImage.builder().profile(p).imgUrl(url).build());
            }
        }
        return toResponse(profileRepository.save(p));
    }

    /**
     * 프로필 등록 (파일 업로드 방식)
     * - categoryIds: List<Long> (숫자 배열) → JSON으로 저장
     * - profile_img: MultipartFile (선택)
     * - desc_images: List<MultipartFile> (선택)
     */
    @Override
    public ProfileResponse createWithFiles(Long currentUserId,
                                           String nickname,
                                           String description,
                                           List<Long> categoryIds,
                                           MultipartFile profileImg,
                                           List<MultipartFile> descImages) {
        if (profileRepository.existsByUser_Id(currentUserId)) {
            throw new IllegalArgumentException("이미 프로필이 등록되어 있습니다.");
        }
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 1) S3 업로드
        String profileImgUrl = null;
        if (profileImg != null && !profileImg.isEmpty()) {
            profileImgUrl = fileStorage.upload(profileImg, "upload/profile");
        }
        List<String> descImageUrls = (descImages == null ? List.of() :
                descImages.stream()
                        .filter(f -> f != null && !f.isEmpty())
                        .map(f -> fileStorage.upload(f, "upload/desc"))
                        .toList());

        // 2) 저장
        Profile p = Profile.builder()
                .user(user)
                .nickname(nickname)
                .description(description)
                .profileImg(profileImgUrl)
                .categoryIds(writeJsonLong(categoryIds))
                .build();

        for (String url : descImageUrls) {
            p.getImages().add(DescImage.builder().profile(p).imgUrl(url).build());
        }

        return toResponse(profileRepository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long currentUserId) {
        Profile p = profileRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));
        return toResponse(p);
    }

    // ===== helpers =====
    private String writeJsonLong(List<Long> list){
        if (list == null) return null;
        try { return om.writeValueAsString(list); }
        catch (Exception e){ return null; }
    }

    private List<Long> readJsonLong(String json){
        if (json == null) return List.of();
        try { return om.readValue(json, new TypeReference<List<Long>>(){}); }
        catch (Exception e){ return List.of(); }
    }
    /** S3 즉시 삭제 (실패해도 서비스 흐름 유지) */
    private void safeDelete(String url) {
        if (!StringUtils.hasText(url)) return;
        try {
            fileStorage.delete(url);
        } catch (Exception e) {

        }
    }

    private ProfileResponse toResponse(Profile p){
        var imgs = p.getImages().stream().map(DescImage::getImgUrl).toList();
        return new ProfileResponse(
                p.getNickname(),
                p.getProfileImg(),
                readJsonLong(p.getCategoryIds()), // 🔹 숫자 배열로 응답
                p.getDescription(),
                imgs,
                p.getTrust().intValue(),
                p.getMatchCount(),
                p.getReportCount()
        );
    }
    @Override
    public ProfileResponse patch(Long currentUserId, ProfilePatchRequest req) {
        Profile p = profileRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));

        // 1) 텍스트 필드 (전달되었을 때만 수정)
        if (req.nickname() != null) p.setNickname(req.nickname());
        if (req.description() != null) p.setDescription(req.description());

        // 2) 비밀번호 (원하면 UserService 주입 후 여기서 변경 호출)
        if (StringUtils.hasText(req.rawPassword())) {
            User me = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
            me.setPassword(passwordEncoder.encode(req.rawPassword())); // BCrypt
            // userRepository.save(me); // @Transactional이면 더티체킹으로 자동 반영, 명시 save 생략 가능
        }
        // 3) 카테고리 (전달 시 교체) — 현재 스키마가 JSON String이라면 기존 헬퍼 활용
        if (req.categoryIds() != null) {
            p.setCategoryIds(writeJsonLong(req.categoryIds()));   // ← 기존 헬퍼 사용 (변경 없이 유지)
        }

        // 4) 프로필 이미지 교체 (S3 업로드 후 기존 즉시 삭제)
        if (req.profileImg() != null && !req.profileImg().isEmpty()) {
            String oldUrl = p.getProfileImg();
            String newUrl = fileStorage.upload(req.profileImg(), "upload/profile");
            p.setProfileImg(newUrl);
            safeDelete(oldUrl);
        }

        // 5) 소개 이미지 삭제 (컬렉션에서 제거 → orphanRemoval 로 DB 삭제, 이후 S3 삭제)
        if (req.descImagesDeleteIds() != null && !req.descImagesDeleteIds().isEmpty()) {
            // 요청 id 집합
            var requestIds = new java.util.HashSet<>(req.descImagesDeleteIds());

            // 내 프로필의 이미지 중 삭제 대상만 추출
            var removeList = p.getImages().stream()
                    .filter(di -> requestIds.contains(di.getId()))
                    .toList();

            // 소유권/존재 검증 (요청 id와 실제 삭제 목록 수가 다르면 오류)
            if (removeList.size() != requestIds.size()) {
                throw new IllegalArgumentException("존재하지 않거나 내 이미지가 아닌 id가 포함되어 있습니다.");
            }

            // 1) 컬렉션에서 제거 → orphanRemoval=true 로 DB에서도 삭제됨
            p.getImages().removeAll(removeList);

            // 2) S3 즉시 삭제
            for (DescImage di : removeList) {
                safeDelete(di.getImgUrl());
            }
        }


        // 6) 소개 이미지 추가 (append)
        if (req.descImagesAdd() != null && !req.descImagesAdd().isEmpty()) {
            req.descImagesAdd().stream()
                    .filter(f -> f != null && !f.isEmpty())
                    .map(f -> fileStorage.upload(f, "upload/desc"))
                    .forEach(url -> p.getImages().add(DescImage.builder().profile(p).imgUrl(url).build()));
        }


        // flush는 트랜잭션 종료 시점
        return toResponse(p);
    }

}
