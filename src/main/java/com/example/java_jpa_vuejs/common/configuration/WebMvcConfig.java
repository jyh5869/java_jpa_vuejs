package com.example.java_jpa_vuejs.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    // Cors 정책을 모두 허용으로 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // 허용할 클라이언트 도메인
                .allowedOrigins("https://www.juso.go.kr") // 허용할 클라이언트 도메인
                
                //.allowedOrigins("*") // 허용할 클라이언트 도메인
                .allowedOriginPatterns("*") // 모든 원본을 허용

                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
    }
}