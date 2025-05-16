package com.example.java_jpa_vuejs.auth.repositoryService;

import java.util.Map;

import org.json.JSONObject;

import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;

public interface SignService {

	Boolean regMember(JoinDto joinDto);
	Members loginMemberMysql(LoginDto loginDto);
	Integer idValidation(LoginDto loginDto);
	void userRegistration(JoinDto joinDto);
	Members getUserInfo(LoginDto loginDto);
	Members getUserInfoEmail(LoginDto loginDto);
	Integer userModify(JoinDto joinDto);
	long getLastIdex();
	Integer userDelete(JoinDto joinDto);
	boolean saveAuthInfo(JSONObject saveAuthInfo);
}
