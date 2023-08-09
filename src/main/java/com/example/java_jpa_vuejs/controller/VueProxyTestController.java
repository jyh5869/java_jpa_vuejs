package com.example.java_jpa_vuejs.controller;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_jpa_vuejs.auth.AuthProvider;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.common.repositoryService.EmailService;
import com.example.java_jpa_vuejs.auth.repositoryService.SignFirebaseService;
import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.auth2.repositoryService.RepositoryService;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;

import com.google.gson.JsonObject;

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

    private final EmailService emailService;

    private final FirebaseConfiguration firebaseConfiguration;

    private final AuthProvider authProvider;
    //private final FirebaseAuthSignUtil firebaseAuthSignUtil;

    /**
    * @method 클라우드를 통한 리스트 호출
    * @param  null
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
        LOG.info("<로그인 시도>");

        AuthenticationDto authentication = new AuthenticationDto();
        
        try {
            authentication = signService.loginMemberMysql(loginDto);
            authentication.setLoginType("DB");
            
        } 
        catch (Exception e) {
            e.printStackTrace();
            LOG.info(" DB AUTH ERROR - CLOUD AUTH START!");
            authentication = signFirebaseService.loginMember(loginDto);
            authentication.setLoginType("CLOUD");
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("accesstoken", authProvider.createToken(authentication));
        responseHeaders.set("refreshtoken", authProvider.createRefreshToken(authentication));

        return ResponseEntity.ok().headers(responseHeaders).body(authentication);
    }

    /**
    * @method 리프래쉬 토큰을 이용한 엑세스 토큰 재발급
    * @param joinDto
    * @throws Exception
    */
    @PostMapping(value = {"/reissuance"})
    public  ResponseEntity<AuthenticationDto> reissuance(@Valid @RequestBody AuthenticationDto joinDto) throws Exception {
        LOG.info("<엑세스 토큰 재 발급>");
        LOG.info("getId       = " + joinDto.getId());
        LOG.info("getEmail    = " + joinDto.getEmail());
        LOG.info("getName     = " + joinDto.getName());
        LOG.info("getMobile   = " + joinDto.getMobile());
        LOG.info("getNickname = " + joinDto.getNickname());

        AuthenticationDto authentication = new AuthenticationDto();
        
        authentication.setId(joinDto.getId());
        authentication.setEmail(joinDto.getEmail());
        authentication.setNickname(joinDto.getNickname());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("accesstoken", authProvider.createToken(authentication));

        return ResponseEntity.ok().headers(responseHeaders).body(authentication);
    }


    /**
    * @method ID 유효성 체크 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/idValidation"})
    public Integer idValidation(LoginDto loginDto) throws Exception {
        LOG.info("<아이디 중복 체크 진입>");
        
        Integer emailCnt;

        try {
            emailCnt = signService.idValidation(loginDto);
            LOG.info("DB / SEARCH EMAIL CNT = " + emailCnt);
        } 
        catch (Exception e) {
            e.printStackTrace();

            emailCnt = signFirebaseService.idValidation(loginDto);
            LOG.info("COULD / SEARCH EMAIL CNT = " + emailCnt);
        }

        return emailCnt;
    }

    /**
    * @method 회원 가입 Insert Action 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/userRegistration"})
    public boolean userRegistration(JoinDto joinDto) throws Exception {
        LOG.info("<회원등록 시작>");
        LOG.info("getId       = " + joinDto.getId());
        LOG.info("getEmail    = " + joinDto.getEmail());
        LOG.info("getName     = " + joinDto.getName());
        LOG.info("getMobile   = " + joinDto.getMobile());
        LOG.info("getNickname = " + joinDto.getNickname());

        boolean returnFlag;
        try {
            joinDto.setId(signService.getLastIdex());
            
            //데이터 베이스에 회원정보 세팅
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
    * @method 회원 정보 가져오기 Select Action 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/getUserInfo"})
    public JoinDto getUserInfo(LoginDto loginDto) throws Exception {
        LOG.info("<회원정보 가져오기>");
        JoinDto joinDto = new JoinDto();
        try {
            Members member = signService.getUserInfo(loginDto);
            joinDto = member.toDto(member);
        } 
        catch (Exception e) {
            e.printStackTrace();
            Members member = signFirebaseService.getUserInfo(loginDto);
            joinDto = member.toDto(member);
        }

        return joinDto;
    }

    /**
    * @method 회원 정보 수정하기 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/userModify"})
    public String userManagement(JoinDto joinDto, @RequestParam(name = "actionType") String actionType) throws Exception {
        LOG.info("<회원정보 수정 진입>");
        LOG.info("getId       = " + joinDto.getId());
        LOG.info("getEmail    = " + joinDto.getEmail());
        LOG.info("getName     = " + joinDto.getName());
        LOG.info("getMobile   = " + joinDto.getMobile());
        LOG.info("getNickname = " + joinDto.getNickname());
        LOG.info("getNickname = " + joinDto.getAuthType());

        boolean returnFlag = false;
        Integer updateCnt = 0;
        String errorCode = null;

        LoginDto loginDto = new LoginDto();
        loginDto.setId(joinDto.getId());
        
        String validationPw = joinDto.getPassword();
        String originPw = signService.getUserInfo(loginDto).getPassword();

        if(!validationPw.equals(originPw)){
            returnFlag = false;
            errorCode = "ERROR01";
        }
        else{
            if(actionType.equals("UPDATE")){
                try {
                    updateCnt = signService.userModify(joinDto);
                    signFirebaseService.userModify(joinDto);
                    
                    returnFlag = true;
                    
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    
                    returnFlag = false;
                    errorCode = "ERROR02";
                }
            }
            else if(actionType.equals("DELETE")){
                try {
                    updateCnt = signService.userDelete(joinDto);
                    signFirebaseService.userDelete(joinDto);
        
                    returnFlag = true; 
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    
                    returnFlag = false;
                    errorCode = "ERROR02";
                }
            }
        }

        //Response Data 세팅 및 RETURN
        JsonObject returnIObj = new JsonObject();
        returnIObj.addProperty("actionType", actionType);
        returnIObj.addProperty("returnFlag", returnFlag);
        returnIObj.addProperty("errorCode", errorCode);
        returnIObj.addProperty("updateCnt", updateCnt);

        return returnIObj.toString();
    }

    /**
    * @method 회원 정보 가져오기 Select Action 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/setSendAuthEmail"})
    public ResponseEntity<LoginDto> setSendAuthEmail(LoginDto loginDto) throws Exception {
        LOG.info("<토큰 생성 후 이메일 발송>");
        /*
         * 1. 토큰생성
         * 2. 비밀변경 URL생성 (아이디 + 토큰)
         * 3. URL을 이메일로 발송
         * 4. URL클릭시 유효기간 체크 후 유효할 경우 비밀번호 변경 페이지 이동
         */

        long tokenValidTime = Duration.ofMinutes(60).toMillis(); // 만료시간 10분인 엑세스 토큰 
        
        try {
            //토큰 생성
            HttpHeaders responseHeaders = new HttpHeaders();

            String validToken = authProvider.createTokenCustom(tokenValidTime, loginDto.getEmail());
            
            responseHeaders.set("accesstoken", validToken);            
            
            
            String confirm = emailService.sendSimpleMessage(loginDto.getEmail(), validToken);

            //sybvifbfdfnighch
            return ResponseEntity.ok().headers(responseHeaders).body(loginDto);
        } 
        catch (Exception e) {
            e.printStackTrace();
            
        }

        return null;
    }
}