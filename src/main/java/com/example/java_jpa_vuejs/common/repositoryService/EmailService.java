package com.example.java_jpa_vuejs.common.repositoryService;

public interface EmailService {
    String sendSimpleMessage(String to, String validToken)throws Exception;
}
