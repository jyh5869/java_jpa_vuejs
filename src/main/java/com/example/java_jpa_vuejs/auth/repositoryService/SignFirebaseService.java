package com.example.java_jpa_vuejs.auth.repositoryService;

import java.util.List;
import java.util.Map;

import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;

import jakarta.validation.Valid;

public interface SignFirebaseService {

	void userRegistration(JoinDto joinDto) throws Exception;

	List<Map<String, Object>> getList() throws Exception;

	void userModify(JoinDto joinDto) throws Exception;

	void userDelete(JoinDto joinDto) throws Exception;

	Members loginMember(@Valid LoginDto loginDto) throws Exception;

	Members getUserInfo(LoginDto loginDto) throws Exception;

	Integer idValidation(LoginDto loginDto) throws Exception;

}
