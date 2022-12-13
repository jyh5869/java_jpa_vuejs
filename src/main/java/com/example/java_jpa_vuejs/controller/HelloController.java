package com.example.java_jpa_vuejs.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    //https://jee-goo.tistory.com/entry/SpringBoot-%EA%B0%9C%EB%B0%9C-%ED%99%98%EA%B2%BD-%EA%B5%AC%EC%84%B1-BackEnd
    //ctr + shife + o 일괄 import 
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() throws InterruptedException, ExecutionException, IOException {
        
        /*
        InputStream serviceAccount = new FileInputStream("C:/Users/all4land/Desktop/java_jpa_vuejs/src/main/resources/FirebaseAdminSDK.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(credentials)
            .build();
        FirebaseApp.initializeApp(options);
        */
        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();

        if(firebaseApps!=null && !firebaseApps.isEmpty()){
            for(FirebaseApp app : firebaseApps){
                if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)){
                    firebaseApp = app;
                }
            }
        }
        else{
            FileInputStream refreshToken = new FileInputStream("C:/Users/all4land/Desktop/java_jpa_vuejs/src/main/resources/FirebaseAdminSDK.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(refreshToken))
                .setDatabaseUrl("https://nodejs-54f7b-default-rtdb.firebaseio.com")
                .build();

                firebaseApp = FirebaseApp.initializeApp(options);

        }
        
        Firestore db = FirestoreClient.getFirestore();


        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("board").get();
        // ...
        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            
            System.out.println("User: " + document.getId());
            System.out.println("brdmemo: " + document.getString("brdmemo"));
            System.out.println("brdwriter: " + document.getString("brdwriter"));
            /*
            if (document.contains("middle")) {
                System.out.println("Middle: " + document.getString("middle"));
            }
            System.out.println("Last: " + document.getString("last"));
            System.out.println("Born: " + document.getLong("born")); 
            System.out.println(document);
            */
        }

        System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        return "Hello, World!";
    }

    // http://localhost:8080/#/ vue 호출
    @RequestMapping(value = "/vue", method = RequestMethod.GET)
    public String index1() {
        return "/index";
    }

    //https://ohrora-developer.tistory.com/25
    //https://doozi0316.tistory.com/entry/Vuejs-Spring-Boot-MySQL-MyBatis-%EB%A7%9B%EC%A7%91-%EC%A7%80%EB%8F%84-%EB%A7%8C%EB%93%A4%EA%B8%B01-Spring-Boot-Vuejs-%EC%84%A4%EC%B9%98-%EB%B0%8F-%EC%97%B0%EB%8F%99%ED%95%98%EA%B8%B0
    @RequestMapping(value = "/api/hello", method = RequestMethod.GET)
    public String helloWorld() {
        System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        return "hello!";
    }
    
}