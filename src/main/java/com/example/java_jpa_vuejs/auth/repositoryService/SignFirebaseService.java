package com.example.java_jpa_vuejs.auth.repositoryService;

import java.util.List;
import java.util.Map;

import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;

public interface SignFirebaseService {

	void userRegistration(JoinDto joinDto) throws Exception;

	List<Map<String, Object>> getList() throws Exception;

	void userModify(JoinDto joinDto) throws Exception;

}
