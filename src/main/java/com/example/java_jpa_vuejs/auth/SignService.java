package com.example.java_jpa_vuejs.auth;

import com.example.java_jpa_vuejs.auth.MemberRepository;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.util.DuplicatedException;
import com.example.java_jpa_vuejs.util.ForbiddenException;
import com.example.java_jpa_vuejs.util.UserNotFoundException;
import com.example.java_jpa_vuejs.validation.Empty;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;

@Service //("signService")
public class SignService {
	
	MemberRepository memberRepository;

	@Lazy
	@Autowired(required=false)
	BCryptPasswordEncoder  passwordEncoder;
	
	@Autowired(required=false)
	ModelMapper modelMapper;


	public Boolean regMember(JoinDto joinDto) {

		// 아이디 중복체크
		if (!Empty.validation(memberRepository.countByEmail(joinDto.getEmail())))
			throw new DuplicatedException("Duplicated ID");

		// 연락처 중복체크
		if (!Empty.validation(memberRepository.countByMobile(joinDto.getMobile())))
			throw new DuplicatedException("Duplicated Mobile");

		// 비밀번호 암호화처리
		joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));

		// 데이터 등록(저장)
		memberRepository.save(joinDto.toEntity());

		return true;
	}
	

	public AuthenticationDto loginMember(LoginDto loginDto) {

		System.out.println("아예 부르지를 못하나?     =     ");
		memberRepository.countByEmail(loginDto.getEmail());
		System.out.println("아예 부르지를 못하나?     =     " + memberRepository.countByEmail(loginDto.getEmail()));
		// dto -> entity
		Members loginEntity = loginDto.toEntity();
		System.out.println(loginEntity.getEmail());
		// 회원 엔티티 객체 생성 및 조회시작
		Members member = memberRepository.findByEmail(loginEntity.getEmail())
				.orElseThrow(() -> new UserNotFoundException("User Not Found"));

		if (!passwordEncoder.matches(loginEntity.getPassword(), member.getPassword()))
			throw new ForbiddenException("Passwords do not match");

		// 회원정보를 인증클래스 객체(authentication)로 매핑

		return modelMapper.map(member, AuthenticationDto.class);
	}

}