package com.example.java_jpa_vuejs.auth.point;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.web.access.AccessDeniedHandler;

import org.springframework.security.access.AccessDeniedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAccessDeniedPoint implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) 
            		throws IOException, ServletException {
    	
        response.sendRedirect("/exception/accessdenied");
    }

}