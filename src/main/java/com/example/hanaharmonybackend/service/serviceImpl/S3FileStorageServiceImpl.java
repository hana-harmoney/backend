package com.example.hanaharmonybackend.service.serviceImpl;

import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import com.example.hanaharmonybackend.service.FileStorageService;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileStorageServiceImpl implements FileStorageService {

    private final S3Template s3Template;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    @Override
    public String upload(MultipartFile file, String dir) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorStatus.INVALID_INPUT);
        }

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.'))
                : "";
        if (ext.length() > 10) ext = ""; // 방어

        String datePath = LocalDate.now().toString().replace("-", "/"); // yyyy/MM/dd
        String baseDir = StringUtils.hasText(dir)
                ? (dir.endsWith("/") ? dir : dir + "/")
                : "upload/";
        String key = baseDir + datePath + "/" + UUID.randomUUID() + ext;

        try {
            // Content-Type 등 메타데이터 지정 (AWSpring ObjectMetadata 사용)
            ObjectMetadata metadata = ObjectMetadata.builder()
                    .contentType(file.getContentType())
                    .build();

            // 업로드 (InputStream + ObjectMetadata)
            s3Template.upload(bucket, key, file.getInputStream(), metadata);

            // 퍼블릭 접근 필요 시, 버킷 정책/CloudFront 설정을 별도로 구성하세요.
            return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.SERVER_ERROR);
        }
    }
    @Override
    public void delete(String urlOrKey) {
        String key = toKey(urlOrKey);
        if (key == null || key.isBlank()) return;
        s3Template.deleteObject(bucket, key);
    }
    private String toKey(String urlOrKey) {
        try {
            if (urlOrKey.startsWith("s3://")) {
                // s3://bucket/key
                int i = urlOrKey.indexOf('/', 5);
                return (i > 0) ? urlOrKey.substring(i + 1) : null;
            }
            if (urlOrKey.startsWith("http")) {
                String path = new URI(urlOrKey).getPath(); // /key...
                return path != null && path.startsWith("/") ? path.substring(1) : path;
            }
            return urlOrKey; // 이미 key라고 가정
        } catch (Exception e) {
            return urlOrKey;
        }
    }
}
