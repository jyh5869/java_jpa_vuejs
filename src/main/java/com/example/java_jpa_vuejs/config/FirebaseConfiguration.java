package com.example.java_jpa_vuejs.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.rpc.context.AttributeContext.Resource;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfiguration {

    @Value("${firebase.sdk.path}")
    private String firebaseSdkPath;
    private FirebaseApp firebaseApp;

    @PostConstruct
    public FirebaseApp initializeFCM() throws IOException {

        //FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();

        if (firebaseApps != null && !firebaseApps.isEmpty()) {
            System.out.println("★★★★");
            for (FirebaseApp app : firebaseApps) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp = app;
                }
            }
        } 
        else {
            System.out.println("☆☆☆☆☆");
            FileInputStream refreshToken = new FileInputStream("C:/Users/all4land/Desktop/java_jpa_vuejs/src/main/resources/FirebaseAdminSDK.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .setDatabaseUrl("https://nodejs-54f7b-default-rtdb.firebaseio.com")
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options);

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