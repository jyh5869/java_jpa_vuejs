package com.example.java_jpa_vuejs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.Util;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;
import com.google.api.core.ApiFuture;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.cloud.FirestoreClient;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class VueProxyTestController {
    
    @Autowired
    private FirebaseConfiguration firebaseConfiguration;

    @GetMapping("/getList")
    public List<Map<String, Object>> index() throws InterruptedException, ExecutionException, IOException {
        
        //파이어 베이스 초기화
        firebaseConfiguration.initializeFCM();
        Firestore db = FirestoreClient.getFirestore();

        //스냅샷 호출 후 리스트 생성(JSON)
        ApiFuture<QuerySnapshot> query = db.collection("board").orderBy("brddate", Direction.DESCENDING).limit(10).get();
        QuerySnapshot querySnapshot = query.get();

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

        for (QueryDocumentSnapshot document : documents) {
            /* ※ 데이터 출력 Example */
            System.out.println("id        : " + document.getId());
            //System.out.println("brdmemo   : " + document.getString("brdmemo"));
            

            String toDayFormat = Util.remakeDate(document.getLong("brddate"),1);
            
            Map<String, Object> data = document.getData();
            data.put("brddate", toDayFormat);

            result.add(data);
        }
        return result;
    }
    final private static Logger LOG = Logger.getGlobal();
    public static final String SECURED_TEXT = "Hello from the secured resource!";

    @GetMapping("/login")
    public void login() {
        LOG.info("GET successfully called on /login resource");
        System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★");
    }

}