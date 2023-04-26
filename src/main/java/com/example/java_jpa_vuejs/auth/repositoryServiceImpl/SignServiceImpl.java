package com.example.java_jpa_vuejs.auth.repositoryServiceImpl;

import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.auth.entity.Phones;
import com.example.java_jpa_vuejs.auth.repositoryJPA.MemberRepository;
import com.example.java_jpa_vuejs.auth.repositoryJPA.PhonesRepository;
import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.util.DuplicatedException;
import com.example.java_jpa_vuejs.util.ForbiddenException;
import com.example.java_jpa_vuejs.util.UserNotFoundException;
import com.example.java_jpa_vuejs.validation.Empty;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;


@Service("signService")
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

	private final MemberRepository memberRepository;
	private final PhonesRepository phonesRepository;

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
		memberRepository.countByEmail(loginEntity.getEmail());
		
		// 회원정보를 인증클래스 객체(authentication)로 매핑
		return 1;
	}

	public void userRegistration(JoinDto joinDto) {

		// DTO -> Entity
		Members joinEntity = joinDto.toEntity();

		Phones phone = new Phones(joinEntity, joinEntity.getMobile());

		// 회원 엔티티 객체 생성 및 조회시작
		memberRepository.save(joinEntity);
		phonesRepository.save(phone);
	}

	public AuthenticationDto loginMemberFirebase(LoginDto loginDto) {
		Members loginEntity = loginDto.toEntity();
		Members member = memberRepository.findByEmail(loginEntity.getEmail())
			.orElseThrow(() -> new UserNotFoundException("User Not Found"));

		return modelMapper.map(member, AuthenticationDto.class);
	}


	@Override
	public Members getUserInfo(LoginDto loginDto) {
		Members loginEntity = loginDto.toEntity();
		Members member = memberRepository.findById(loginEntity.getEmail())
			.orElseThrow(() -> new UserNotFoundException("User Not Found"));

		return member;
	}

}