package com.example.java_jpa_vuejs.auth.point;

import com.example.java_jpa_vuejs.auth.AuthProvider;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * JWT TOKEN 인증 필터
 */
public class CustomFilter extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(CustomFilter.class);

    private AuthProvider jwtTokenProvider;

    public CustomFilter(AuthProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest)request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        
        LOG.info("스프링 시큐리티 Before Filter2 Action!");
        try {
            if("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
                httpRes.setStatus(HttpServletResponse.SC_OK);
            } 
            else {
                LOG.info("Access Token : "+httpReq.getHeader("accesstoken"));
            	
                // 사용자 인증토큰 검사
                String token = jwtTokenProvider.resolveToken(httpReq, "accesstoken");
                if (token != null) {

                    if(jwtTokenProvider.validateToken(token)) {
                        LOG.info("Access Token : Exist");
                        Authentication auth = jwtTokenProvider.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                    else{
                        String reftreshToken = jwtTokenProvider.resolveToken(httpReq, "refreshtoken");

                        if(jwtTokenProvider.validateToken(reftreshToken)) {
                            httpRes.setHeader("retry", "true");
                        }
                        else{
                            httpRes.setHeader("retry", "false");
                        }
                        
                        LOG.info("Access Token : Expiration");
                        /*
                        401(Unauthorized) - httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            상태: 클라이언트가 인증되지 않았거나, 유효한 인증 정보가 부족하여 요청이 거부됨
                            예시: 사용자가 로그인되지 않은 경우
                        403(Forbidden) - httpRes.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            상태: 서버가 해당 요청을 이해했지만, 권한이 없어 요청이 거부됨
                            예시: 사용자가 권한이 없는 요청을 하는 경우
                        */
                        httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                        return;
                    }
                }
                else{
                    LOG.info("Access Token : Empty");
                    /* 
                    httpRes.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                    */
                }
                filterChain.doFilter(request, response);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
