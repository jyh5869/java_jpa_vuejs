package com.example.java_jpa_vuejs.auth.point;

import com.example.java_jpa_vuejs.auth.AuthProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

//@RequiredArgsConstructor
public class CustomFilter extends GenericFilterBean {

    private AuthProvider jwtTokenProvider;

    
    public CustomFilter(AuthProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest)request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        try {
            if("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
                httpRes.setStatus(HttpServletResponse.SC_OK);
            } else {
                System.out.println("accessToken--------------------------------->"+httpReq.getHeader("accesstoken"));
            	/** 사용자 인증토큰 검사 */
                String token = jwtTokenProvider.resolveToken(httpReq);
                if (token != null) {
                    System.out.println("----------------------------------------->TOKEN YES");
                    if(jwtTokenProvider.validateToken(token)) {
                        Authentication auth = jwtTokenProvider.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                else{
                    System.out.println("----------------------------------------->TOKEN NO");
                }
                filterChain.doFilter(request, response);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
