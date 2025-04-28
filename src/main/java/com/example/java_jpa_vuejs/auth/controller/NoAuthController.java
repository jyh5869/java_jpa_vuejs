package com.example.java_jpa_vuejs.auth.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.example.java_jpa_vuejs.auth.repositoryService.RedirectService;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.java_jpa_vuejs.auth.authUtil.SEED_ENC;
import com.example.java_jpa_vuejs.auth.authUtil.SHA256;

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
    private final String callbackBase = "http://localhost:8000/api/noAuth/callback";

    public NoAuthController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @PostMapping("/generate-request-info")
    public Map<String, String> generateRequestInfo(
            @RequestBody Map<String, String> req) throws UnsupportedEncodingException {
        String redirectTo = req.getOrDefault("redirectTo", "/");
        String reqSvcCd   = "01";
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
    public ResponseEntity<String> callback(@RequestParam Map<String,String> params) {
      // 모든 파라미터를 JSON으로 직렬화
      JsonObject json = new JsonObject();
      params.forEach((k,v) -> json.addProperty(k, v));

      // 부모 창 origin을 하드코딩
      String parentOrigin = "http://localhost:8080";

      String html =
        "<!DOCTYPE html>\n" +
        "<html><head><meta charset=\"UTF-8\">\n" +
        // favicon 요청 차단
        "<link rel=\"icon\" href=\"data:;base64,=\" />\n" +
        "</head><body>\n" +
        "<script>\n" +
        "  const payload = " + json.toString() + ";\n" +
        "  // 부모(origin 8080)로 메시지 전송\n" +
        "  window.opener.postMessage(\n" +
        "    { type: 'INICIS_AUTH_COMPLETE', payload },\n" +
        "    '" + parentOrigin + "'\n" +
        "  );\n" +
        "  window.close();\n" +
        "</script>\n" +
        "<p>인증이 완료되어 창을 닫습니다...</p>\n" +
        "</body></html>";

      return ResponseEntity.ok()
                           .contentType(MediaType.TEXT_HTML)
                           .body(html);
  }
}