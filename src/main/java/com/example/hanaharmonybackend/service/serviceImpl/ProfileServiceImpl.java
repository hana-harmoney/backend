package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.domain.DescImage;
import com.example.hanaharmonybackend.domain.Profile;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.repository.ProfileRepository;
import com.example.hanaharmonybackend.repository.UserRepository;
import com.example.hanaharmonybackend.service.FileStorageService;
import com.example.hanaharmonybackend.service.ProfileService;
import com.example.hanaharmonybackend.web.dto.ProfileCreateRequest;
import com.example.hanaharmonybackend.web.dto.ProfileResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.hanaharmonybackend.payload.code.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorage;   // S3 업로드/삭제
    private final ObjectMapper om = new ObjectMapper();

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
                .categoryIds(writeJsonLong(req.category_ids())) // 🔹 숫자 배열로 저장
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
                                           List<Long> categoryIds,                 // 🔹 Long으로 변경
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
                .categoryIds(writeJsonLong(categoryIds)) // 🔹 숫자 배열로 저장
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
}
