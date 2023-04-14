package com.example.java_jpa_vuejs.auth;

import com.example.java_jpa_vuejs.util.DuplicatedException;
import com.example.java_jpa_vuejs.util.ForbiddenException;
import com.example.java_jpa_vuejs.util.UserNotFoundException;
import com.example.java_jpa_vuejs.validation.Empty;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;


@Service("signService")
@RequiredArgsConstructor
public class SignService {

	private final MemberRepository memberRepository;
	//private final BCryptPasswordEncoder passwordEncoder;	
	private final ModelMapper modelMapper;
	

	public Boolean regMember(JoinDto joinDto) {

		// 아이디 중복체크
		if (!Empty.validation(memberRepository.countByEmail(joinDto.getEmail())))
			throw new DuplicatedException("Duplicated ID");

		// 연락처 중복체크
		if (!Empty.validation(memberRepository.countByMobile(joinDto.getMobile())))
			throw new DuplicatedException("Duplicated Mobile");

		// 비밀번호 암호화처리
		//joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));

		// 데이터 등록(저장)
		memberRepository.save(joinDto.toEntity());

		return true;
	}
	

	public AuthenticationDto loginMemberMysql(LoginDto loginDto) {

		// DTO -> Entity
		Members loginEntity = loginDto.toEntity();
		System.out.println(loginEntity.getEmail());
		// 회원 엔티티 객체 생성 및 조회시작
		Members member = memberRepository.findByEmail(loginEntity.getEmail())
				.orElseThrow(() -> new UserNotFoundException("User Not Found"));
		
		/* 
		if (!passwordEncoder.matches(loginEntity.getPassword(), member.getPassword())){
			throw new ForbiddenException("Passwords do not match");
		}
		*/
		if (!loginEntity.getPassword().equals(member.getPassword())){
			throw new ForbiddenException("Passwords do not match");
		}
			
		// 회원정보를 인증클래스 객체(authentication)로 매핑
		return modelMapper.map(member, AuthenticationDto.class);
	}

	public Integer idValidation(LoginDto loginDto) {

		// DTO -> Entity
		Members loginEntity = loginDto.toEntity();
		System.out.println(loginEntity.getEmail());
		// 회원 엔티티 객체 생성 및 조회시작
		Integer emailCnt = memberRepository.countByEmail(loginEntity.getEmail());
		
			
		// 회원정보를 인증클래스 객체(authentication)로 매핑
		return emailCnt;
	}

	public Integer userRegistration(JoinDto joinDto) {

		// DTO -> Entity
		Members loginEntity = joinDto.toEntity();
		System.out.println(loginEntity.getEmail());
		// 회원 엔티티 객체 생성 및 조회시작
		memberRepository.userRegistration(joinDto);
		
			
		// 회원정보를 인증클래스 객체(authentication)로 매핑
		return 1;
	}

	public AuthenticationDto loginMemberFirebase(LoginDto loginDto) {
		Members loginEntity = loginDto.toEntity();
		Members member = memberRepository.findByEmail(loginEntity.getEmail())
			.orElseThrow(() -> new UserNotFoundException("User Not Found"));

		return modelMapper.map(member, AuthenticationDto.class);
	}

}