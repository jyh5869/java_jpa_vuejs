package com.example.java_jpa_vuejs.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.modelmapper.ModelMapper;

@Configuration
public class SpringConfig {
    
    /**
     * ModelMapper 사용을 위한 Bean 생성
     */
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
    
    /**
     * BCryptPasswordEncoder 사용을 위한 Bean 생성 (비밀번호 암호화)
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}