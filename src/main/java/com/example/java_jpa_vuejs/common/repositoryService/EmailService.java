package com.example.java_jpa_vuejs.common.repositoryService;

import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;

public interface EmailService {
    String sendSimpleMessage(JoinDto joinDto, String validToken)throws Exception;
    String sendSimpleMessage2(LoginDto loginDto, String validToken)throws Exception;
}
