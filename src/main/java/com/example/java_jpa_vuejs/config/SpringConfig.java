package com.example.java_jpa_vuejs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

import com.example.java_jpa_vuejs.auth.MemberRepository;

@Configuration
public class SpringConfig {
    
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
    
}