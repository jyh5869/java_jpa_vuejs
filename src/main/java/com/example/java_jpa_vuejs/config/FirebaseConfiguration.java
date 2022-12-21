package com.example.java_jpa_vuejs.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import jakarta.annotation.PostConstruct;

@Component
@Configuration
public class FirebaseConfiguration {

    @Value("${custom.firebase.sdk.path}")
    private String firebaseSdkPath;//SDK파일 경로

    @Value("${custom.firebase.database.url}")
    private String firebaseDatabaseUrl;//DBURL

    private FirebaseApp firebaseApp;

    @PostConstruct
    public FirebaseApp initializeFCM() throws IOException {

        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
        
    if (firebaseApps != null && !firebaseApps.isEmpty()) {
            for (FirebaseApp app : firebaseApps) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp = app;
                }
            }
        } 
        else {
            FileInputStream refreshToken = new FileInputStream(firebaseSdkPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .setDatabaseUrl(firebaseDatabaseUrl)
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options);
            System.out.println(firebaseApp);
        }
        return firebaseApp;
    }

    @Bean
    public FirebaseAuth initFirebaseAuth() {
        FirebaseAuth instance = FirebaseAuth.getInstance(firebaseApp);
        return instance;
    }

    @Bean
    public FirebaseMessaging initFirebaseMessaging() {
        FirebaseMessaging instance = FirebaseMessaging.getInstance(firebaseApp);
        return instance;
    }

}