package com.example.java_jpa_vuejs.auth.point;

import com.example.java_jpa_vuejs.auth.AuthProvider;

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

        try {
            if("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
                httpRes.setStatus(HttpServletResponse.SC_OK);
            } 
            else {
                LOG.info("Access Token : "+httpReq.getHeader("accesstoken"));
            	
                // 사용자 인증토큰 검사
                String token = jwtTokenProvider.resolveToken(httpReq);
                if (token != null) {
                    LOG.info("Access Token : Exist");
                    //토큰 유효성 검사
                    if(jwtTokenProvider.validateToken(token)) {
                        Authentication auth = jwtTokenProvider.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                else{
                    LOG.info("Access Token : Empty");
                }
                filterChain.doFilter(request, response);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
