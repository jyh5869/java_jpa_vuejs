package com.example.java_jpa_vuejs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.Util;
import com.example.java_jpa_vuejs.auth.AuthProvider;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.SignService;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VueProxyTestController {

    final private static Logger LOG = Logger.getGlobal();
    public static final String SECURED_TEXT = "Hello from the secured resource!";

    /* 생성자 생성 방법2
    @Autowired
    private FirebaseConfiguration firebaseConfiguration;
    */

    private final FirebaseConfiguration firebaseConfiguration;
    private final SignService apiSignService;
    private final AuthProvider authProvider;

    /**
    * @method 클라우드를 통한 리스트 호출
    * @param 
    * @throws Exception
    */
    @GetMapping("/getList")
    public List<Map<String, Object>> index() throws InterruptedException, ExecutionException, IOException {
        
        long reqTime = Util.durationTime ("start", "JPA 테스트", 0, "Proceeding" );
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

        try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("board").orderBy("brddate", Direction.DESCENDING).limit(10).get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            

            for (QueryDocumentSnapshot document : documents) {
                /* ※ 데이터 출력 Example 
                System.out.println("id        : " + document.getId());
                */

                String toDayFormat = Util.remakeDate(document.getLong("brddate"),1);
                
                Map<String, Object> data = document.getData();
                data.put("brddate", toDayFormat);

                result.add(data);
            }
            Util.durationTime ("end", "JPA 테스트", reqTime, "Complete" );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "JPA 테스트", reqTime, "Fail" );
            e.printStackTrace();
        }

        return result;
    }

    
    /**
    * @method 로그인
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/signin"})
    public ResponseEntity<AuthenticationDto> appLogin(@Valid @RequestBody LoginDto loginDto) throws Exception {

        AuthenticationDto authentication = new AuthenticationDto();

        try {
            authentication = apiSignService.loginMemberMysql(loginDto);
        } 
        catch (Exception e) {
            authentication = apiSignService.loginMemberFirebase(loginDto);
        }
        

        return ResponseEntity.ok()
                .header("accesstoken", authProvider
                .createToken(
                    authentication.getId(),
                    authentication.getEmail(),
                    "USER"))
                .body(authentication);
    }
}