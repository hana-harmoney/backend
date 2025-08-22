package com.example.hanaharmonybackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /** dir 예: "upload/profile", "upload/desc" */
    String upload(MultipartFile file, String dir);
}
