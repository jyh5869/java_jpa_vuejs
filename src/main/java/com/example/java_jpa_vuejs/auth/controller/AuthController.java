package com.example.java_jpa_vuejs.auth.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.json.JSONParser;
import org.hibernate.mapping.Join;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_jpa_vuejs.auth.AuthProvider;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.authUtil.SEED_ENC;
import com.example.java_jpa_vuejs.auth.authUtil.SHA256;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.common.configuration.FirebaseConfiguration;
import com.example.java_jpa_vuejs.common.model.PaginationDto;
import com.example.java_jpa_vuejs.common.repositoryService.EmailService;
import com.example.java_jpa_vuejs.common.repositoryService.PaginationFirebaseService;
import com.example.java_jpa_vuejs.common.utility.PaginationAsyncCloud;
import com.example.java_jpa_vuejs.auth.repositoryService.SignFirebaseService;
import com.example.java_jpa_vuejs.auth.repositoryService.SignService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    final private static Logger LOG = Logger.getGlobal();

    public static final String SECURED_TEXT = "Hello from the secured resource!";

    /* 생성자 생성 방법2
    @Autowired
    private FirebaseConfiguration firebaseConfiguration;
    */

    private final ModelMapper modelMapper;

    private final SignService signService;
    private final SignFirebaseService signFirebaseService;
    private final PaginationFirebaseService paginationFirebaseService;

    private final EmailService emailService;

    private final FirebaseConfiguration firebaseConfiguration;
    
    private final AuthProvider authProvider;


    /**
    * @method 클라우드를 통한 리스트 호출 테스트
    * @param  null
    * @throws Exception
    */
    @GetMapping("/getList")
    public Map<String, Object> index(@Valid PaginationDto paginationDto) throws Exception {

        Map<String, Object> retMap = new HashMap<String, Object>();
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();

        paginationDto.setCurrentPage(URLDecoder.decode(paginationDto.getCurrentPage().trim(), "UTF-8"));
        paginationDto.setCollectionNm("board");
        paginationDto.setOrderbyCol("brddate");
 
        paginationDto.setTotalCount(paginationFirebaseService.getTotalCount(paginationDto));
        paginationDto.setFirstDoc(paginationFirebaseService.getFirstDoc(paginationDto));
        paginationDto.setPrevDoc(paginationFirebaseService.getPrevDoc(paginationDto));
        paginationDto.setLastDoc(paginationFirebaseService.getLastDoc(paginationDto));
        paginationDto.setDocIdArr(paginationFirebaseService.getDocIdList(paginationDto));
        paginationDto.setNextDoc(paginationFirebaseService.getNextDoc(paginationDto));

        String pagination = PaginationAsyncCloud.getDividePageFormByParams(paginationDto);

        retList = signFirebaseService.getList(paginationDto);   

        retMap.put("list", retList);
        retMap.put("pagination", pagination);
        retMap.put("setDocIdArr", paginationDto.getDocIdArr());

        return retMap;
    }

    /**
    * @method 로그인
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/signin"})
    public ResponseEntity<AuthenticationDto> appLogin(@Valid @RequestBody LoginDto loginDto) throws Exception {
        LOG.info("<로그인 시도>");

        // 1.객체 및 변수 선언
        AuthenticationDto authentication = new AuthenticationDto(); //유저 권한인즈으 객체(리턴용)
        Members member = new Members();                             //회원 정보 객체(정보 가져오기용)
        String loginType = "";                                      //로그인 타입 1.DB / 2.CLOUD
        String loginRes  = "";                                      //로그인 결과 1.SUCCESS / 2.FAIL

        // 2.입력된 아이디로 유저정보 가져오기
        try {
            // 1] - MYSQL에서 아이디로 정보 조회 (정보가 없거나 통신 실패시 예외 발생)
            member = signService.loginMemberMysql(loginDto);
            loginType = "DB";
        }
        catch (Exception e) {
            // 2] - MYSQL에서 정보조회 실패시 FIREBASE로 조회
            LOG.info(" DB AUTH ERROR - CLOUD AUTH START!");

            member = signFirebaseService.loginMember(loginDto);
            loginType = "CLOUD";

            e.printStackTrace();
        }

        // 3.비밀번호 일치 및 사용가능(Y/N) 검증 후 인증 객체 리턴(IF: 실패, ELSE: 성공)
        if (!loginDto.getPassword().equals(member.getPassword()) || member.getDeleteYn().equals("Y")){
            /* 비밀번호 불일치 시 예외를 발생시켜 처리하는 코드(운영적 관점에서 부적합)
			   throw new ForbiddenException("Passwords do not match");
            */
            //로그인 결과 세팅 후 리턴
            authentication.setLoginRes("FAIL");
            return ResponseEntity.ok().body(authentication);
		}
        else{
            //유저 정보 및 로그인 결과 세팅 
            authentication = modelMapper.map(member, AuthenticationDto.class);
            authentication.setLoginType(loginType);
            authentication.setLoginRes("SUCCESS");

            //응답 객체 생성 및 세팅 후 리턴
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("accesstoken", authProvider.createToken(authentication));
            responseHeaders.set("refreshtoken", authProvider.createRefreshToken(authentication));

            return ResponseEntity.ok().headers(responseHeaders).body(authentication);
        }
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
    * @method 회원 정보 가져오기 Select Action 
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/getUserInfoAuthEmail"})
    public JoinDto getUserInfoFromEmail(JoinDto joinDto) throws Exception {
        LOG.info("<회원정보 가져오기 (이메일 인증)>");

        LoginDto loginDto = new LoginDto();
        JoinDto joinDtoRes = new JoinDto();

        loginDto.setId(joinDto.getId());

        String token = joinDto.getToken();
        String actionType = joinDto.getActionType();
        
        //토큰 유효성 검사 후 회원정보 조회
        if(authProvider.validateToken(token)) {
            System.out.println("토큰 유효성 체크 완료 : TARGETID  -------------> " + joinDto.getId());

            boolean returnFlag = false;
            Integer updateCnt = 0;
            String errorCode = null;

            if(actionType.equals("DETAIL")){
                
                try {
                    Members member = signService.getUserInfo(loginDto);
                    joinDtoRes = member.toDto(member);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Members member = signFirebaseService.getUserInfo(loginDto);
                    joinDtoRes = member.toDto(member);
                }
            }
            else if(actionType.equals("UPDATE")){

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

                joinDtoRes.setActionResCd(returnFlag);
                joinDtoRes.setErrorCd(errorCode);
                joinDtoRes.setActionCnt(updateCnt);
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

                joinDtoRes.setActionResCd(returnFlag);
                joinDtoRes.setErrorCd(errorCode);
                joinDtoRes.setActionCnt(updateCnt);
            }
        }
        else{
            System.out.println("토큰 유효성 기긴 만료");
        }

        return joinDtoRes;
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
    @PostMapping(value = {"/noAuth/setSendAuthEmail"})
    public ResponseEntity<LoginDto> setSendAuthEmail(LoginDto loginDto) throws Exception {
        LOG.info("<토큰 생성 후 이메일 발송>");
        /*
         * 1. 토큰생성
         * 2. 비밀변경 URL생성 (아이디 + 토큰)
         * 3. URL을 이메일로 발송
         * 4. URL클릭시 유효기간 체크 후 유효할 경우 비밀번호 변경 페이지 이동
         */
        
        HttpHeaders responseHeaders = new HttpHeaders();//응답 헤더 생성
        try {
            //응답 해더 생성 및 이메일 유효성 판단
            long tokenValidTime = Duration.ofMinutes(60).toMillis(); // 만료시간 10분인 엑세스 토큰 
            Integer emailCnt = signService.idValidation(loginDto);

            if(emailCnt == 1){
                //1. 찾을 유저정보 가져오기 및 세팅
                Members member = signService.getUserInfoEmail(loginDto);
                JoinDto joinDto = member.toDto(member);
                
                //토큰 생성 및 이메일 발송
                String validToken = authProvider.createTokenCustom(tokenValidTime, loginDto.getEmail());
                String confirmStr = emailService.sendSimpleMessage(joinDto, validToken);

                responseHeaders.set("state-code", "SUCCESS00");
                
                return ResponseEntity.ok().headers(responseHeaders).body(loginDto);
            }
            else{
                responseHeaders.set("state-code", "ERROR01");            
                return ResponseEntity.ok().headers(responseHeaders).body(loginDto);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            
            responseHeaders.set("state-code", "ERROR00");            
            return ResponseEntity.ok().headers(responseHeaders).body(loginDto);
        }
    }
    
    /**
    * @method 아이디로 사용될 이메일 유효성 체크
    * @param loginDto
    * @throws Exception
    */
    @PostMapping(value = {"/noAuth/chkEmailValidity"})
    public ResponseEntity<LoginDto> chkEmailValidity(LoginDto loginDto) throws Exception {
        LOG.info("<토큰 생성 후 이메일 발송>");
        /*
         * 1. 토큰생성
         * 2. 비밀변경 URL생성 (아이디 + 토큰)
         * 3. URL을 이메일로 발송
         * 4. URL클릭시 유효기간 체크 후 유효할 경우 비밀번호 변경 페이지 이동
         */
        
        HttpHeaders responseHeaders = new HttpHeaders();//응답 헤더 생성
        try {
            //응답 해더 생성 및 이메일 유효성 판단
            long tokenValidTime = Duration.ofMinutes(10).toMillis(); // 만료시간 10분인 엑세스 토큰 
            Integer emailCnt = signService.idValidation(loginDto);

            //올바른 이메일입니다. 해당이메일로 이메일을 발송하겠습니다.
            if(emailCnt == 0){
                
                //토큰 생성 및 이메일 발송
                String validToken = authProvider.createTokenCustom(tokenValidTime, loginDto.getEmail());
                String confirmStr = emailService.sendSimpleMessage2(loginDto, validToken);

                responseHeaders.set("state-code", "SUCCESS00");
                responseHeaders.set("authentication-code", confirmStr);

                return ResponseEntity.ok().headers(responseHeaders).body(loginDto);
            }
            else{//이미 존재하는 아이디 입니다. 아이디 중복체크를 완료해 주세요.
                responseHeaders.set("state-code", "ERROR01");            
                return ResponseEntity.ok().headers(responseHeaders).body(loginDto);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            //이메일 인증과정 알수없는 문제가 발생하였습니다. 잠시 후 다시 시도해 주세요.
            responseHeaders.set("state-code", "ERROR00");            
            return ResponseEntity.ok().headers(responseHeaders).body(loginDto);
        }
    }
/* 
    @PostMapping("/noAuth/generate-request-info")
    public Map<String, String> generateRequestInfo(@RequestBody Map<String, String> req, HttpSession session) {
        //@RequestParam(value="redirectTo", defaultValue="#/my-posts") String redirectTo

        

        // 2) 인증 완료 후 돌아갈 클라이언트 라우트 저장
        String redirectTo = req.get("redirectTo");
        session.setAttribute("redirectAfterAuth", redirectTo);
        System.out.println("☆ ------------- redirectTo = " + redirectTo);

        String callbackBase = "http://localhost:8000/api/noAuth/callback";
        // URL-encode
        String callbackUrl = callbackBase + "?redirectTo=" + redirectTo;


        String mid = "INIiasTest";
        String apiKey = "TGdxb2l3enJDWFRTbTgvREU3MGYwUT09";
        //String mid = "ltmetrcias";
        //String apiKey = "fd3a2c94846b6b1087322934db8895b6";

        String reqSvcCd = "01";
        String mTxId = "mTxId_" + System.currentTimeMillis();
        String reservedMsg = "isUseToken=Y";

        String userName = req.get("userName");
        String userPhone = req.get("userPhone");
        String userBirth = req.get("userBirth");
        String flgFixedUser = "Y";

        SHA256 hasher = new SHA256();
        String authHash = hasher.sha256(mid + mTxId + apiKey);
        String userHash = hasher.sha256(userName + mid + userPhone + mTxId + userBirth + reqSvcCd);

        Map<String, String> result = new HashMap<>();
        result.put("mid", mid);
        result.put("reqSvcCd", reqSvcCd);
        result.put("mTxId", mTxId);
        result.put("authHash", authHash);
        result.put("flgFixedUser", flgFixedUser);
        result.put("userName", userName);
        result.put("userPhone", userPhone);
        result.put("userBirth", userBirth);
        result.put("userHash", userHash);
        result.put("reservedMsg", reservedMsg);
        result.put("successUrl", callbackUrl);
        result.put("failUrl", callbackUrl);
        //result.put("successUrl", "http://localhost:8000/api/noAuth/success");
        //result.put("failUrl", "http://localhost:8000/api/noAuth/fail");
        return result;
    }

    @RequestMapping(
        value    = "/noAuth/callback",
        method   = { RequestMethod.GET, RequestMethod.POST },
        produces = "text/html; charset=UTF-8"
    )
    public ResponseEntity<String> callback(@RequestParam Map<String,String> params, HttpSession session, HttpServletRequest request) {

        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("인증 완료!@!@!@!@!@");
        // 1) txId 검증, 2) CI 복호화 → session.setAttribute("userCi", userCi);

        // 1) 본인인증 SA가 보내주는 파라미터들
        //String resultCode     = request.getParameter("resultCode");
        //String authRequestUrl = request.getParameter("authRequestUrl");
        //String mTxId          = request.getParameter("mTxId");
        //String token          = request.getParameter("token");
        // 2) 쿼리스트링으로 넘어온 redirectTo
        String redirectTo     = "#"+request.getParameter("redirectTo");
        // 3) 클라이언트가 원래 요청한 경로
        //String redirectTo = (String) session.getAttribute("redirectAfterAuth");
        //session.removeAttribute("redirectAfterAuth");

        System.out.println("redirectToredirectToredirectToredirectToredirectTo == " + redirectTo);

        if (redirectTo == null || redirectTo.isBlank()) {
            redirectTo = "/";  // 기본 경로
        }

        // 4) 팝업에서 실행할 JS: 메인 창 리다이렉트 후 팝업 닫기
        String script = """
        <html><body>
            <script>
            // alert("인증창 닫을게요.);
            // 부모 창(메인) 이동
            window.opener.location.href = '%s';
            // 팝업 닫기
            window.close();
            </script>
            <p>인증이 완료되어 창을 닫습니다...</p>
        </body></html>
        """.formatted(redirectTo);

        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(script);
    }
*/
    @PostMapping("/noAuth/myList")
    public void getMyPosts(HttpSession session) {
        System.out.println("내 글쓰기 진입");
    }

    @PostMapping(value = "/noAuth/success", produces = "text/html; charset=UTF-8")
    public String handleSuccess(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("성공!!!");
        try {
            request.setCharacterEncoding("UTF-8");

            String resultCode = request.getParameter("resultCode");
            String resultMsg = request.getParameter("resultMsg");

            if (!"0000".equals(resultCode)) {
                return "<p>인증 실패 결과</p><p>" + resultCode + "</p><p>" + resultMsg + "</p>";
            }

            String authRequestUrl = request.getParameter("authRequestUrl");
            String txId = request.getParameter("txId");
            String token = request.getParameter("token");

            //authRequestUrl = URLDecoder.decode(resultMsg, "UTF-8");
            //txId = URLDecoder.decode(resultMsg, "UTF-8");
            //token = URLDecoder.decode(resultMsg, "UTF-8");

            System.out.println("[DEBUG] authRequestUrl = " + authRequestUrl);
            System.out.println("[DEBUG] txId = " + txId);
            System.out.println("[DEBUG] resultCode = " + resultCode);
            System.out.println("[DEBUG] resultMsg = " + resultMsg);

            if (authRequestUrl == null || !(authRequestUrl.startsWith("https://kssa.inicis.com") || authRequestUrl.startsWith("https://fcsa.inicis.com"))) {
                return "<p>인증 서버 URL이 유효하지 않습니다. [" + authRequestUrl + "]</p>";
            }

            JsonObject reqJson = new JsonObject();
            reqJson.addProperty("mid", "INIiasTest"); // 실서비스에서는 상점 MID로 대체
            reqJson.addProperty("txId", txId);

            URL url = new URL(authRequestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(9999);

            OutputStream os = conn.getOutputStream();
            os.write(reqJson.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            
            int responseCode = conn.getResponseCode();
            System.out.println("[DEBUG] 이니시스 응답코드 = " + responseCode);

            if (responseCode == 403) {
                return "<p>이니시스 서버에서 Access Denied 응답을 받았습니다. MID 또는 txId를 확인하세요.</p>";
            }

            JsonObject resJson = null;
            if (responseCode == HttpServletResponse.SC_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                resJson = JsonParser.parseString(sb.toString()).getAsJsonObject();
            }

            StringBuilder html = new StringBuilder();
            html.append("<html><head><meta charset='UTF-8'><title>인증결과</title>");
            html.append("<style>body{font-family:sans-serif;}table{border-collapse:collapse;margin:20px auto;}td,th{border:1px solid #ccc;padding:8px;}</style></head><body>");
            html.append("<h2>본인인증 결과</h2><table>");

            if (resJson != null) {
                String decryptedUserName = "";
                String decryptedUserCi = "";

                try {
                    decryptedUserName = new SEED_ENC().decrypt(resJson.get("userName").getAsString(), token);
                    decryptedUserCi = new SEED_ENC().decrypt(resJson.get("userCi").getAsString(), token);

                    decryptedUserName = URLDecoder.decode(decryptedUserName, "UTF-8");
                    decryptedUserCi = URLDecoder.decode(decryptedUserCi, "UTF-8");
                } catch (Exception e) {
                    System.out.println("[ERROR] 복호화 실패: " + e.getMessage());
                }

                for (Map.Entry<String, com.google.gson.JsonElement> entry : resJson.entrySet()) {
                    html.append("<tr><th>").append(entry.getKey()).append("</th><td>").append(entry.getValue().getAsString()).append("</td></tr>");
                }

                html.append("<tr><th>복호화된 사용자 이름</th><td>").append(decryptedUserName).append("</td></tr>");
                html.append("<tr><th>복호화된 CI</th><td>").append(decryptedUserCi).append("</td></tr>");
            }

            html.append("</table></body></html>");
            return html.toString();
            

        } catch (Exception e) {
            e.printStackTrace();
            return "<p>처리 중 오류 발생: " + e.getMessage() + "</p>";
        }
    }

    @PostMapping(value = "/noAuth/fail", produces = "text/html; charset=UTF-8")
    public String handleFail(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("실패!!!");
        try {
            request.setCharacterEncoding("UTF-8");
            String resultCode = request.getParameter("resultCode");
            String resultMsg = request.getParameter("resultMsg");

            String decodedMsg = URLDecoder.decode(resultMsg, "UTF-8");

            StringBuilder html = new StringBuilder();
            html.append("<html><head><meta charset='UTF-8'><title>인증 실패</title>");
            html.append("<style>body{font-family:sans-serif;text-align:center;margin-top:50px;}p{font-size:18px;}</style></head><body>");
            html.append("<h2>본인인증 실패</h2>");
            html.append("<p>실패 사유 코드: ").append(resultCode).append("</p>");
            html.append("<p>실패 메시지: ").append(decodedMsg).append("</p>");
            html.append("</body></html>");

            return html.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "<p>처리 중 오류 발생: " + e.getMessage() + "</p>";
        }
    }
}