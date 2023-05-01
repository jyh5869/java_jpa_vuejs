package com.example.java_jpa_vuejs.auth.repositoryService;

import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;

public interface SignService {
	Boolean regMember(JoinDto joinDto);
	AuthenticationDto loginMemberMysql(LoginDto loginDto);
	Integer idValidation(LoginDto loginDto);
	void userRegistration(JoinDto joinDto);
	AuthenticationDto loginMemberFirebase(LoginDto loginDto);
	Members getUserInfo(LoginDto loginDto);
	Integer userModify(JoinDto joinDto);
}
