package com.example.java_jpa_vuejs.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private AuthProvider jwtTokenProvider;

    public JwtAuthenticationFilter(AuthProvider jwtTokenProvider) {
        System.out.println("tokken--------------------------------------------------------------");
        System.out.println(jwtTokenProvider);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("tokken22222222222222222222222222222--------------------------------------------------------------");
        HttpServletRequest httpReq = (HttpServletRequest)request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        try {
            if("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
                httpRes.setStatus(HttpServletResponse.SC_OK);
            } else {
                System.out.println("tokken22222222222222222222222222222--------------------------------------------------------------");
                String token = jwtTokenProvider.resolveToken(httpReq);
                System.out.println("tokken22222222222222222222222222222--------------------------------------------------------------");
                if (token != null) {
                    if(jwtTokenProvider.validateToken(token)) {
                        System.out.println("tokken22222222222222222222222222222--------------------------------------------------------------");
                        /** 사용자 인증토큰 검사 */
                        Authentication auth = jwtTokenProvider.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                filterChain.doFilter(request, response);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
