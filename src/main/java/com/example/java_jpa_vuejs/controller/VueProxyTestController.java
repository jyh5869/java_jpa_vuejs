package com.example.java_jpa_vuejs.controller;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.Util;
import com.example.java_jpa_vuejs.auth.AuthProvider;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.MemberRepository;
import com.example.java_jpa_vuejs.auth.SignService;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;
import com.google.api.core.ApiFuture;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.logging.Logger;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VueProxyTestController {
    /*
    @Autowired
    private FirebaseConfiguration firebaseConfiguration;
    
    @Autowired
    SignService apiSignService;
    
    @Autowired
    AuthProvider authProvider;
    */
    private final FirebaseConfiguration firebaseConfiguration;
    private final SignService apiSignService;
    private final AuthProvider authProvider;

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

    @GetMapping("/logout")
    public void logout(HttpSession session) {
        LOG.info("GET successfully called on /logout resource");
        //Object securityContextObj = session.getAttribute("SPRING_SECURITY_CONTEXT");

        //System.out.println(securityContextObj);


        System.out.println("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
    }
    
    
    /**
    * @method 설명 : 로그인
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/signin"})
    public ResponseEntity<AuthenticationDto> appLogin(@Valid @RequestBody LoginDto loginDto) throws Exception {
        System.out.println("★★★★★★★★★★★");
        AuthenticationDto authentication = apiSignService.loginMember(loginDto);
        System.out.println("★★★★★★★★★★★");
        System.out.println(authentication);
        return ResponseEntity.ok()
                .header("accesstoken", authProvider
                .createToken(
                    authentication.getId(),
                    authentication.getEmail(),
                    "USER"))
                .body(authentication);
    }
}