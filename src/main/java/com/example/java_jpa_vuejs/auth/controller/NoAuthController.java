package com.example.java_jpa_vuejs.auth.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.java_jpa_vuejs.auth.repositoryService.RedirectService;
import com.fasterxml.jackson.core.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.java_jpa_vuejs.auth.authUtil.SEED_ENC;
import com.example.java_jpa_vuejs.auth.authUtil.SHA256;

import org.springframework.http.HttpHeaders;
import com.example.java_jpa_vuejs.auth.authUtil.NiceID.CPClient;

//import NiceID.Check.CPClient;

/**
 * ① generate-request-info: 인증창에 넘길 파라미터 생성
 * ② callback: Inicis → 콜백, mTxId 로 원래 SPA 라우트 꺼내서 팝업 닫고 부모창 해시 이동
 */
@RestController
@RequestMapping("/api/noAuth")
public class NoAuthController {

    private final RedirectService redirectService;
    
    private final String mid         = "INIiasTest";
    private final String apiKey      = "TGdxb2l3enJDWFRTbTgvREU3MGYwUT09";

    //private final String mid         = "ltmetrcias";
    //private final String apiKey      = "fd3a2c94846b6b1087322934db8895b6";

    private final String callbackBase = "http://localhost:8000/api/noAuth/callback";

    public NoAuthController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @PostMapping("/generate-request-info")
    public Map<String, String> generateRequestInfo( @RequestBody Map<String, String> req) throws UnsupportedEncodingException {

        String redirectTo = req.getOrDefault("redirectTo", "/");
        String reqSvcCd   = "03";//["01":간편인증, "02":전자서명, "03":본인확인] ※02 (전자서명) 일 경우, identifier 파라미터 추가 세팅필요	

        String mTxId      = "mTxId_" + System.currentTimeMillis();
        String reservedMsg= "isUseToken=Y";
        String userName   = req.get("userName");
        String userPhone  = req.get("userPhone");
        String userBirth  = req.get("userBirth");
        String flgFixedUser = "Y";

        // 해시 생성
        SHA256 hasher = new SHA256();
        String authHash = hasher.sha256(mid + mTxId + apiKey);
        String userHash = hasher.sha256(userName + mid + userPhone + mTxId + userBirth + reqSvcCd);

        // callback URL에 redirectTo 쿼리 인코딩 포함
        String encodedRedirect = URLEncoder.encode(redirectTo, StandardCharsets.UTF_8.toString());
        String callbackUrl = callbackBase + "?redirectTo=" + encodedRedirect;

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
        return result;
    }


     /**
     * Inicis 인증 완료 후 호출되는 콜백.
     * 모든 쿼리 파라미터를 JSON 객체로 만들어 popup.opener.postMessage 로 전달합니다.
     */
    @RequestMapping(
        value    = "/callback",
        method   = { RequestMethod.GET, RequestMethod.POST },
        produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> callback(@RequestParam Map<String,String> params, HttpServletRequest request) throws IOException {
       try {
        // 1) UTF-8 세팅
        request.setCharacterEncoding("UTF-8");

        // 2) 콜백 파라미터 꺼내기
        String resultCode     = request.getParameter("resultCode");
        String resultMsg      = request.getParameter("resultMsg");
        String authRequestUrl = request.getParameter("authRequestUrl");
        String txId           = request.getParameter("txId");
        String token          = request.getParameter("token");
        String redirectTo     = request.getParameter("redirectTo");

        // 3) 인증 실패 시
        if (!"0000".equals(resultCode)) {
            // resultMsg 는 percent‐encoding 되어 오므로 디코딩
            String decodedMsg = URLDecoder.decode(resultMsg, "UTF-8");
            JSONObject payload = new JSONObject();
            payload.put("resultCode", resultCode);
            payload.put("resultMsg",  resultMsg);
            payload.put("redirectTo", redirectTo);

            String htmlFail =
              "<!DOCTYPE html>\n" +
              "<html><head><meta charset=\"UTF-8\"></head><body>\n" +
              "<script>\n" +
              "  window.opener.postMessage(\n" +
              "    { type: 'INICIS_AUTH_COMPLETE', payload: " + payload.toString() + " },\n" +
              "    '*'\n" +
              "  );\n" +
              "  window.close();\n" +
              "</script>\n" +
              "</body></html>";

            return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                                 .body(htmlFail);
        }

        // 4) getResultData.do 보정
        if (!authRequestUrl.endsWith(".do")) {
            authRequestUrl = authRequestUrl.replaceFirst(
                "/getResultData.*$", "/getResultData.do"
            );
        }

        // 5) JSON 바디 준비 (org.json)
        JSONObject reqJson = new JSONObject();
        reqJson.put("mid",  mid);
        reqJson.put("txId", txId);

        // 6) HTTP POST(JSON) 요청
        URL url = new URL(authRequestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(9999);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(reqJson.toString().getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();

        // 7) 응답 읽기
        BufferedReader br = new BufferedReader(new InputStreamReader(
            responseCode == HttpURLConnection.HTTP_OK
                ? conn.getInputStream()
                : conn.getErrorStream(),
            StandardCharsets.UTF_8
        ));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        // 8) JSON 파싱 (org.json)
        JSONObject resJson = new JSONObject(sb.toString());

        // 8-1) sysout 으로 키값들 찍기
        System.out.println("== Inicis ResultData ==");
        System.out.println("resultCode    : " + resJson.optString("resultCode"));
        System.out.println("resultMsg     : " + resJson.optString("resultMsg"));
        System.out.println("txId          : " + resJson.optString("txId"));
        System.out.println("mTxId         : " + resJson.optString("mTxId"));
        System.out.println("svcCd         : " + resJson.optString("svcCd"));
        System.out.println("providerDevCd : " + resJson.optString("providerDevCd"));
        System.out.println("userName      : " + resJson.optString("userName"));
        System.out.println("userPhone     : " + resJson.optString("userPhone"));
        System.out.println("userBirthday  : " + resJson.optString("userBirthday"));
        System.out.println("userCi        : " + resJson.optString("userCi"));
        System.out.println("signedData    : " + resJson.optString("signedData"));
        System.out.println("userGender    : " + resJson.optString("userGender"));
        System.out.println("isForeign     : " + resJson.optString("isForeign"));
        System.out.println("userDi        : " + resJson.optString("userDi"));
        System.out.println("userCi2       : " + resJson.optString("userCi2"));
        System.out.println("redirectTo    : " + resJson.optString("redirectTo"));
        System.out.println("==========================");

        // 8-2) resultMsg URL 디코딩
        if (resJson.has("resultMsg")) {
            resJson.put("resultMsg", URLDecoder.decode(resJson.getString("resultMsg"), "UTF-8"));
        }

        // 9) SEED 복호화 + URL 디코딩 (키별 처리)
        SEED_ENC seed = new SEED_ENC();
        if (resJson.has("userName")) {
          String dec = seed.decrypt(resJson.getString("userName"), token);
          String utf8 = URLDecoder.decode(dec, "UTF-8");
          resJson.put("userName", utf8);
          System.out.println("userName (dec)    : " + dec);
          System.out.println("userName (dec)    : " + utf8);
      }
      if (resJson.has("userPhone")) {
          String dec = seed.decrypt(resJson.getString("userPhone"), token);
          String utf8 = URLDecoder.decode(dec, "UTF-8");
          resJson.put("userPhone", utf8);
          System.out.println("userPhone (dec)    : " + utf8);
      }
      if (resJson.has("userBirthday")) {
          String dec = seed.decrypt(resJson.getString("userBirthday"), token);
          String utf8 = URLDecoder.decode(dec, "UTF-8");
          resJson.put("userBirthday", utf8);
          System.out.println("userBirthday (dec) : " + utf8);
      }
      if (resJson.has("userCi")) {
          String dec = seed.decrypt(resJson.getString("userCi"), token);
          String utf8 = URLDecoder.decode(dec, "UTF-8");
          resJson.put("userCi", utf8);
          System.out.println("userCi (dec)       : " + utf8);
      }
      if (resJson.has("signedData")) {
          String dec = seed.decrypt(resJson.getString("signedData"), token);
          String utf8 = URLDecoder.decode(dec, "UTF-8");
          resJson.put("signedData", utf8);
          System.out.println("signedData (dec)   : " + utf8);
      }
      if (resJson.has("userGender")) {
          String dec = seed.decrypt(resJson.getString("userGender"), token);
          String utf8 = URLDecoder.decode(dec, "UTF-8");
          resJson.put("userGender", utf8);
          System.out.println("userGender (dec)   : " + utf8);
      }
      if (resJson.has("isForeign")) {
          String dec = seed.decrypt(resJson.getString("isForeign"), token);
          String utf8 = URLDecoder.decode(dec, "UTF-8");
          resJson.put("isForeign", utf8);
          System.out.println("isForeign (dec)    : " + utf8);
      }
      if (resJson.has("userDi")) {
          String dec = seed.decrypt(resJson.getString("userDi"), token);
          String utf8 = URLDecoder.decode(dec, "UTF-8");
          resJson.put("userDi", utf8);
          System.out.println("userDi (dec1)       : " + utf8);
          System.out.println("userDi (dec2)       : " + resJson.getString("userDi"));
      }
       
      if (resJson.has("userCi2")) {
          //String dec = seed.decrypt(resJson.getString("userCi2"), token);
          //String utf8 = URLDecoder.decode(dec, "UTF-8");
          //resJson.put("userCi2", utf8);
          System.out.println("userCi2 (dec)      : " + resJson.getString("userCi2"));
      }
        
        // 10) redirectTo 추가
        resJson.put("redirectTo", redirectTo);

        // 11) 팝업 → 부모창 postMessage
        String parentOrigin = "http://localhost:8080";  // Vue 앱 origin
        String html =
          "<!DOCTYPE html>\n" +
          "<html><head><meta charset=\"UTF-8\"></head><body>\n" +
          "<script>\n" +
          "  window.opener.postMessage(\n" +
          "    { type: 'INICIS_AUTH_COMPLETE', payload: " + resJson.toString() + " },\n" +
          "    '" + parentOrigin + "'\n" +
          "  );\n" +
          "  window.close();\n" +
          "</script>\n" +
          "</body></html>";

        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                             .body(html);

    } catch (Exception e) {
        e.printStackTrace();
        String err = "<p>처리 중 오류 발생: " + e.getMessage() + "</p>";
        return ResponseEntity.status(500)
                            .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                             .body(err);
    }
}

    private final String siteCode         = "G7817";
    private final String returnUrl      = "http://localhost:8000/api/noAuth/callback/nice";
    private final String sitePassword = "UN1UOX4TSJAT";
    private final String errorUrl = "http://localhost:8000/api/noAuth/callback/nice";

    @PostMapping("/request/nice")
    public ResponseEntity<Map<String, String>> getEncData(HttpSession session , @RequestBody Map<String, String> request) throws UnsupportedEncodingException {
        System.out.println(" 나 이 스 본 인 인 증 진 입");

        String redirectTo = request.getOrDefault("redirectTo", "/");
        System.out.println("redirectTo = " + redirectTo);
        
        // callback URL에 redirectTo 쿼리 인코딩 포함
        //String encodedRedirect = URLEncoder.encode(redirectTo, StandardCharsets.UTF_8.toString());
        String callbackUrl = returnUrl + "?redirectTo=" + redirectTo;

        
        CPClient niceCheck = new CPClient();
        String sRequestNumber = niceCheck.getRequestNO(siteCode);
        session.setAttribute("REQ_SEQ", sRequestNumber); // 세션 저장

        String authType = "";
        String customize = "";

        String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber
                + "8:SITECODE" + siteCode.getBytes().length + ":" + siteCode
                + "9:AUTH_TYPE" + authType.getBytes().length + ":" + authType
                + "7:RTN_URL" + callbackUrl.getBytes().length + ":" + callbackUrl
                + "7:ERR_URL" + errorUrl.getBytes().length + ":" + errorUrl
                + "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize;

        int iReturn = niceCheck.fnEncode(siteCode, sitePassword, sPlainData);

        if (iReturn == 0) {
            return ResponseEntity.ok(Map.of(
                "encData", niceCheck.getCipherData(),
                "siteCode", siteCode
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of("error", "암호화 실패 코드: " + iReturn));
        }
            
    }

    /**
     * Inicis 인증 완료 후 호출되는 콜백.
     * 모든 쿼리 파라미터를 JSON 객체로 만들어 popup.opener.postMessage 로 전달합니다.
    */
    @RequestMapping(
        value    = "/callback/nice",
        method   = { RequestMethod.GET, RequestMethod.POST },
        produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> callbackNice(@RequestParam Map<String,String> params, HttpServletRequest request) throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");

            String redirectTo = request.getParameter("redirectTo");
            String encodeData = request.getParameter("EncodeData");

            if (encodeData == null || encodeData.isEmpty()) {
                return ResponseEntity.badRequest().body("<p>파라미터 누락</p>");
            }

            // 디코딩 수행
            com.example.java_jpa_vuejs.auth.authUtil.NiceID.CPClient niceCheck = new com.example.java_jpa_vuejs.auth.authUtil.NiceID.CPClient();
            //String siteCode = "YOUR_SITE_CODE"; // TODO: 외부 설정으로 교체
            //String sitePassword = "YOUR_SITE_PASSWORD";

            int result = niceCheck.fnDecode(siteCode, sitePassword, encodeData);

            JSONObject payload = new JSONObject();
            payload.put("resultCode", result);
            if (result == 0) {
                String plainData = niceCheck.getPlainData();
                payload.put("plainData", plainData);
                Map<String, String> dataMap = niceCheck.fnParse(plainData);
                if (dataMap != null) {
                    System.out.println("== NICE 응답 결과 ==");
                    for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                        payload.put(entry.getKey(), entry.getValue());
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                    }
                    System.out.println("====================");
                }
            } else {
                payload.put("errorMsg", "NICE 복호화 실패: 코드 " + result);
                System.out.println("[ERROR] NICE 복호화 실패: 코드 " + result);
            }

            // 10) redirectTo 추가
            payload.put("redirectTo", redirectTo);
            System.out.println( "redirectTo ------------------------------- > "+redirectTo);
            // JSON 문자열 안전 삽입
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"UTF-8\"></head><body>" +
                    "<script>" +
                    "  window.opener.postMessage({ type: 'NICE_AUTH_COMPLETE', payload: " +
                    //payload.toString().replace("\"", "\\\"") +
                    JSONObject.quote(payload.toString()) +
                    " }, '*');" +
                    "  window.close();" +
                    "</script>" +
                    "</body></html>";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body(html);

        } catch (Exception e) {
            e.printStackTrace();
            String err = "<p>오류 발생: " + e.getMessage() + "</p>";
            return ResponseEntity.status(500)
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body(err);
        }
    }
}