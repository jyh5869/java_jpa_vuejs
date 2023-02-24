package com.example.java_jpa_vuejs.auth;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtExceptionFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
       
        try {
            LOG.info("스프링 시큐리티 Before Filter1 Action!");
            filterChain.doFilter(req, res); // go to 'AuthProvider'
        } 
        catch (JwtException ex) {

            setErrorResponse(HttpStatus.SC_UNAUTHORIZED, res, ex);
        }
    }

    public void setErrorResponse(int scUnauthorized, HttpServletResponse res, Throwable ex) throws IOException {
        LOG.info("스프링 시큐리티 Before Filter1 Action! - Exception!");
        
        /* 
        res.setContentType("application/json; charset=UTF-8");
        jwtExceptionResponse jwtExceptionResponse = new JwtException(ex.getMessage(), HttpStatus.SC_UNAUTHORIZED);
        res.getWriter().write(jwtExceptionResponse.convertToJson());
        */
    }

}