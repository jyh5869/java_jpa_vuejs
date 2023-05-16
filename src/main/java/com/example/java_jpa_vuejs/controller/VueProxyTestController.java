package com.example.java_jpa_vuejs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.Util;
import com.example.java_jpa_vuejs.auth.AuthProvider;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.FirebaseAuthSignUtil;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.auth.repositoryService.SignFirebaseService;
import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.auth2.repositoryService.RepositoryService;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.auth.FirebaseAuth;
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

    private final RepositoryService rs;

    private final SignService signService;
    private final SignFirebaseService signFirebaseService;

    private final FirebaseConfiguration firebaseConfiguration;

    private final AuthProvider authProvider;
    //private final FirebaseAuthSignUtil firebaseAuthSignUtil;

    /**
    * @method 클라우드를 통한 리스트 호출
    * @param 
    * @throws Exception
    */
    @GetMapping("/getList")
    public List<Map<String, Object>> index() throws Exception {

        List<Map<String, Object>> result = signFirebaseService.getList();

        return result;
    }

    
    /**
    * @method 로그인
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/signin"})
    public ResponseEntity<AuthenticationDto> appLogin(@Valid @RequestBody LoginDto loginDto) throws Exception {
        System.out.println("로그인할게용 ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        AuthenticationDto authentication = new AuthenticationDto();
        
        try {
            authentication = signService.loginMemberMysql(loginDto);
        } 
        catch (Exception e) {
            authentication = signService.loginMemberFirebase(loginDto);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("accesstoken", authProvider.createToken(authentication));
        responseHeaders.set("refreshtoken", authProvider.createRefreshToken(authentication));

        return ResponseEntity.ok().headers(responseHeaders).body(authentication);
    }

    @PostMapping(value = {"/reissuance"})
    public  ResponseEntity<AuthenticationDto> reissuance(@Valid @RequestBody AuthenticationDto joinDto) throws Exception {

        System.out.println("토큰을 재 발행 할게요!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        AuthenticationDto authentication = new AuthenticationDto();
        
        authentication.setId(joinDto.getId());
        authentication.setEmail(joinDto.getEmail());
        authentication.setNickname(joinDto.getNickname());

        System.out.println("getId       = " + joinDto.getId());
        System.out.println("getEmail    = " + joinDto.getEmail());
        System.out.println("getName     = " + joinDto.getName());
        System.out.println("getMobile   = " + joinDto.getMobile());
        System.out.println("getNickname = " + joinDto.getNickname());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("accesstoken", authProvider.createToken(authentication));

        return ResponseEntity.ok().headers(responseHeaders).body(authentication);
    }

    /**
    * @method 권한 추가
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/signup"})
    public Void appSineUp(LoginDto loginDto) throws Exception {
        System.out.println("로그인 기능 을 만들거에요.");

        FirebaseAuth firebaseAuth = firebaseConfiguration.initFirebaseAuth();
        System.out.println(firebaseAuth);
        FirebaseAuthSignUtil fbas = new FirebaseAuthSignUtil();
        //fbas.createUser(firebaseAuth);
        //fbas.getUserById(firebaseAuth, "1GRjJfHbIWVKFT6s3BtMv8c6P6t1");

        return null;
    }

    /**
    * @method ID 유효성 체크 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/idValidation"})
    public Integer idValidation(LoginDto loginDto) throws Exception {
        System.out.println("아이디 중복체크 기능을 만들거에요.");

        Integer emailCont = signService.idValidation(loginDto);
        System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        System.out.println(emailCont);

        return emailCont;
    }

    /**
    * @method 회원 가입 Insert Action 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/userRegistration"})
    public boolean userRegistration(JoinDto joinDto) throws Exception {
        System.out.println("회원가입 기능을 만들거에요.");
        System.out.println("getEmail    = " + joinDto.getEmail());
        System.out.println("getPassword = " + joinDto.getPassword());
        System.out.println("getName     = " + joinDto.getName());
        System.out.println("getMobile   = " + joinDto.getMobile());
        System.out.println("getNickname = " + joinDto.getNickname());
        System.out.println("getNickname = " + joinDto.getProfile());

        //rs.deletAll();
        //rs.saveMember();
        //rs.print();
        //rs.lazyPrint();
        //rs.lazyPrint2();

        boolean returnFlag;
        try {
            signService.userRegistration(joinDto);
            signFirebaseService.userRegistration(joinDto);

            returnFlag = true; 
        } 
        catch (Exception e) {
            e.printStackTrace();
            
            returnFlag = false;
        }

        return returnFlag;
    }

    /**
    * @method 회원 정보 가져오기 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/getUserInfo"})
    public JoinDto getUserInfo(LoginDto loginDto) throws Exception {
        System.out.println("회원 정보를 가져오는 서비스를 만들거에요");

        Members member = signService.getUserInfo(loginDto);
        System.out.println(member);
        //LoginDto loginDto = member.;
        JoinDto joinDto = member.toDto(member);

        return joinDto;
    }

    /**
    * @method 회원 정보 가져오기 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/userModify"})
    public Integer userManagement(JoinDto joinDto) throws Exception {
        System.out.println("회원정보 수정 기능을 만들거에요.");

        System.out.println("getId       = " + joinDto.getId());
        System.out.println("getEmail    = " + joinDto.getEmail());
        System.out.println("getPassword = " + joinDto.getPassword());
        System.out.println("getName     = " + joinDto.getName());
        System.out.println("getMobile   = " + joinDto.getMobile());
        System.out.println("getNickname = " + joinDto.getNickname());
        System.out.println("getNickname = " + joinDto.getProfile());

        
        boolean returnFlag;
        Integer updateCnt = 0;
        try {
            updateCnt = signService.userModify(joinDto);
            signFirebaseService.userModify(joinDto);

            returnFlag = true; 
        } 
        catch (Exception e) {
            e.printStackTrace();
            
            returnFlag = false;
        }

        return updateCnt;
    }

}