package com.example.hanaharmonybackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FcmInitializer {
    @Value("${fcm.service-account-path}")
    private String serviceAccountPath;

    @Value("${fcm.project-id}")
    private String projectId;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        InputStream serviceAccountStream;

        try {
            if (serviceAccountPath.startsWith("classpath:")) {
                serviceAccountStream = this.getClass().getClassLoader()
                        .getResourceAsStream(serviceAccountPath.substring("classpath:".length()));

                if (serviceAccountStream == null) {
                    throw new IllegalArgumentException(
                            "Firebase service account file not found in classpath: " + serviceAccountPath);
                }
            } else {
                serviceAccountStream = new FileInputStream(serviceAccountPath);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .setProjectId(projectId)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            return FirebaseMessaging.getInstance();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize FirebaseMessaging", e);
        }
    }
}