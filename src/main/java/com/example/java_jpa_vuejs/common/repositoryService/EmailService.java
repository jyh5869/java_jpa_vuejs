package com.example.java_jpa_vuejs.common.repositoryService;

import com.example.java_jpa_vuejs.auth.JoinDto;

public interface EmailService {
    String sendSimpleMessage(JoinDto joinDto, String validToken)throws Exception;
}
