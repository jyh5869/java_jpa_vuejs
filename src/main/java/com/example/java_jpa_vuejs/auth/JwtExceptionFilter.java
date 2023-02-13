package com.example.java_jpa_vuejs.auth;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("※※※※※※※※※※※※※※※※※※※※※※※※※※※    필터 1");
        try {
            System.out.println("☆☆☆☆☆☆☆111111111111111111111");
            filterChain.doFilter(req, res); // go to 'JwtAuthenticationFilter'
            System.out.println("☆☆☆☆☆☆☆222222222222222222222");
        } 
        catch (JwtException ex) {
            setErrorResponse(HttpStatus.SC_UNAUTHORIZED, res, ex);
        }
    }

    public void setErrorResponse(int scUnauthorized, HttpServletResponse res, Throwable ex) throws IOException {
        System.out.println("※※※※※※※※※※※※※※※※※※※※※※※※※※※    필터 1");
        /* 
        res.setContentType("application/json; charset=UTF-8");

        jwtExceptionResponse jwtExceptionResponse = new JwtException(ex.getMessage(), HttpStatus.SC_UNAUTHORIZED);
        res.getWriter().write(jwtExceptionResponse.convertToJson());
        */
    }

}