package com.example.java_jpa_vuejs.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@RestController
public class HelloController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<QueryDocumentSnapshot> index() throws InterruptedException, ExecutionException, IOException {

        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();

        if (firebaseApps != null && !firebaseApps.isEmpty()) {
            for (FirebaseApp app : firebaseApps) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp = app;
                }
            }
        } 
        else {
            FileInputStream refreshToken = new FileInputStream("C:/Users/all4land/Desktop/java_jpa_vuejs/src/main/resources/FirebaseAdminSDK.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .setDatabaseUrl("https://nodejs-54f7b-default-rtdb.firebaseio.com")
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options);
        }

        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> query = db.collection("board").get();
        QuerySnapshot querySnapshot = query.get();

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        for (QueryDocumentSnapshot document : documents) {

            System.out.println("User: " + document.getId());
            System.out.println("brdmemo: " + document.getString("brdmemo"));
            System.out.println("brdwriter: " + document.getString("brdwriter"));
        }

        return documents;
    }

    @RequestMapping(value = "/helloworld", method = RequestMethod.GET)
    public String helloWorld() {
        return "hello world";
    }
}