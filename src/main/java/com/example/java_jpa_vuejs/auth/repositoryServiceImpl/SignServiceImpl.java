package com.example.java_jpa_vuejs.auth.repositoryServiceImpl;

import com.example.java_jpa_vuejs.auth.AuthDto;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.AuthInfo;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.auth.entity.Phones;
import com.example.java_jpa_vuejs.auth.repositoryJPA.AuthInfoRepository;
import com.example.java_jpa_vuejs.auth.repositoryJPA.MemberRepository;
import com.example.java_jpa_vuejs.auth.repositoryJPA.PhonesRepository;
import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.common.utility.DuplicatedException;
import com.example.java_jpa_vuejs.common.utility.ForbiddenException;
import com.example.java_jpa_vuejs.common.utility.UserNotFoundException;
import com.example.java_jpa_vuejs.common.validation.Empty;

import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;


@Service("signService")
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

	private final MemberRepository memberRepository;
	private final PhonesRepository phonesRepository;
	private final AuthInfoRepository authInfoRepository;

	private final ModelMapper modelMapper;
	
	/*
	 * 회원 가입
	 */
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
	

	/*
	 * 로그인 처리
	 */
	public Members loginMemberMysql(LoginDto loginDto) {

		// DTO -> Entity
		Members loginEntity = loginDto.toEntity();
		System.out.println(loginEntity.getEmail());
		// 회원 엔티티 객체 생성 및 조회시작
		Members member = memberRepository.findByEmailAndDeleteYn(loginEntity.getEmail(), "N")
				.orElseThrow(() -> new UserNotFoundException("User Not Found"));
		
		/* 
		if (!passwordEncoder.matches(loginEntity.getPassword(), member.getPassword())){
			throw new ForbiddenException("Passwords do not match");
		}
		*/
		
		
		// 회원정보를 인증클래스 객체(authentication)로 매핑
		//return modelMapper.map(member, AuthenticationDto.class);
		return modelMapper.map(member, Members.class);
	}

	/*
	 * 아이디 중복 체크
	 */
	public Integer idValidation(LoginDto loginDto) {

		//DTO -> Entity
		Members loginEntity = loginDto.toEntity();
		
		//아이디 중복 체크 결과 리턴
		return memberRepository.countByEmail(loginEntity.getEmail());
	}

	/*
	 * 회원정보 가져오기
	 */
	public Members getUserInfoEmail(LoginDto loginDto) {

		Members loginEntity = loginDto.toEntity();
		Members member = memberRepository.findByEmail(loginEntity.getEmail())
			.orElseThrow(() -> new UserNotFoundException("User Not Found"));

		return member;
	}

	/*
	 * 회원 가입
	 */
	public void userRegistration(JoinDto joinDto) {

		//DTO -> Entity
		Members joinEntity = joinDto.toEntity();

		Phones phone = new Phones(joinEntity, joinEntity.getMobile());

		//회원 정보 저장 (Member, Phone Repository)
		memberRepository.save(joinEntity);
		phonesRepository.save(phone);
	}

	/*
	 * 회원 정보 가져오기
	 */
	@Override
	public Members getUserInfo(LoginDto loginDto) {
		Members loginEntity = loginDto.toEntity();
		Members member = memberRepository.findById(loginEntity.getId())
			.orElseThrow(() -> new UserNotFoundException("User Not Found"));

		return member;
	}

	/*
	 * 회원 정보수정
	 */
	@Override
	public Integer userModify(JoinDto joinDto) {
		Members memberEntity = joinDto.toEntity();

		Optional<Members> member = memberRepository.findById(memberEntity.getId());
		
		if(member.isEmpty()){
			return 0;
		}
		else{
			memberRepository.save(memberEntity);
			return 1;
		}
	}

	/*
	 * 회원가입 테이블 마지막 인덱스 조회
	 */
	@Override
	public long getLastIdex() {
		long lastIdx = memberRepository.getLastIdex();
		
		return lastIdx; 
	}

	/*
	 * 회원 정보삭제
	 */
	@Override
	public Integer userDelete(JoinDto joinDto) {

		String deleteId = String.valueOf(joinDto.getId());

		Integer result = memberRepository.setDeleteUser(deleteId);
		System.out.println("UPDATE CNT FROM JPA : " + result);
		Optional<Members> member = memberRepository.findById(deleteId);
		
		if(member.isEmpty()){
			return 1;
		}
		else{
			return 0;
		}
	}

	/*
	 * 회원 가입
	 */
	public boolean saveAuthInfo(JSONObject saveAuthInfo) {
		
		AuthDto authDto = new AuthDto();
		System.out.println("--------------------본 인 인 증 결 과 저 장 할 게 요-----------------");

		if (saveAuthInfo != null) {
			Iterator<String> keys = saveAuthInfo.keys();
			
			String authProvider = saveAuthInfo.optString("authProvider", "");
			authDto.setAuthProvider(authProvider);
			
			if(authProvider.equals("KG")){
				while (keys.hasNext()) {
					String key = keys.next();
					String value = saveAuthInfo.optString(key, "");  // 값이 없을 경우 "" 반환
			
					System.out.println(key + " : " + value);
					
					switch (key) {
						case "userPhone":
							authDto.setMobileNo(value);
							break;
						case "MOBILE_CO":
							authDto.setMobileCo(value);
							break;
						case "userDi":
							authDto.setUserDi(value);
							break;
						case "userCi":
							authDto.setUserCi(value);
							break;
						case "userName":
							authDto.setUserName(value);
							break;
						case "userGender":
							authDto.setGender(value);
							break;
						case "RES_SEQ":
							authDto.setResSeq(value);
							break;
						case "userBirthday":
							authDto.setBirthdate(value);
							break;
						case "NATIONALINFO":
							authDto.setNationalInfo(value);
							break;
						case "AUTH_TYPE":
							authDto.setAuthType(value);
							break;
					}
				}
			}
			else if(authProvider.equals("NICE")){
				while (keys.hasNext()) {
					String key = keys.next();
					String value = saveAuthInfo.optString(key, "");  // 값이 없을 경우 "" 반환
			
					System.out.println(key + " : " + value);
			
					switch (key) {
						case "MOBILE_NO":
							authDto.setMobileNo(value);
							break;
						case "MOBILE_CO":
							authDto.setMobileCo(value);
							break;
						case "DI":
							authDto.setUserDi(value);
							break;
						case "CI":
							authDto.setUserCi(value);
							break;
						case "UTF8_NAME":
							authDto.setUserName(value);
							break;
						case "GENDER":
							authDto.setGender(value);
							break;
						case "RES_SEQ":
							authDto.setResSeq(value);
							break;
						case "BIRTHDATE":
							authDto.setBirthdate(value);
							break;
						case "NATIONALINFO":
							authDto.setNationalInfo(value);
							break;
						case "AUTH_TYPE":
							authDto.setAuthType(value);
							break;
					}
				}
			}
		}

		authDto.setAuthMgtSn(authInfoRepository.getLastIdex());

		AuthInfo authEntity = authDto.toEntity();

		//회원 정보 저장 (Member, Phone Repository)
		authInfoRepository.save(authEntity);
		
		return true;
	}
}