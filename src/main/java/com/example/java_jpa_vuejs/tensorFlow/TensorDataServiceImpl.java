package com.example.java_jpa_vuejs.tensorFlow;

import org.springframework.stereotype.Service;

import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.tensorFlow.TensorDataService;
import com.example.java_jpa_vuejs.util.DuplicatedException;
import com.example.java_jpa_vuejs.validation.Empty;

import lombok.RequiredArgsConstructor;

@Service("tensorDataService")
@RequiredArgsConstructor
public class TensorDataServiceImpl implements TensorDataService {@Override
    /*
	 * 회원 가입
	 */
	public void getAddrData() {

		// 아이디 중복체크
		//if (!Empty.validation(memberRepository.countByEmail(joinDto.getEmail())))
		//	throw new DuplicatedException("Duplicated ID");

		// 데이터 등록(저장)
		//memberRepository.save(joinDto.toEntity());

	}
    
}
