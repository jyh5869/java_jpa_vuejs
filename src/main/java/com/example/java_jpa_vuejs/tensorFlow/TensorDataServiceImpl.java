package com.example.java_jpa_vuejs.tensorFlow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.geomBoard.repositoryJPA.GeometryBoardRepository;
import com.example.java_jpa_vuejs.tensorFlow.TensorDataService;
import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;
import com.example.java_jpa_vuejs.util.DuplicatedException;
import com.example.java_jpa_vuejs.validation.Empty;
import com.example.java_jpa_vuejs.tensorFlow.repositoryJPA.RoadRepository;

import lombok.RequiredArgsConstructor;

@Service("tensorDataService")
@RequiredArgsConstructor
public class TensorDataServiceImpl implements TensorDataService {
	
	private final RoadRepository roadsRepository;
	/*
	 * 회원 가입
	 */
	public void getAddrData() {

		// 아이디 중복체크
		//if (!Empty.validation(memberRepository.countByEmail(joinDto.getEmail())))
		//	throw new DuplicatedException("Duplicated ID");

		// 데이터 등록(저장)
		//memberRepository.save(joinDto.toEntity());


		Pageable pageable =  PageRequest.of(1, 10);//페이징 객체 선언
		Page<Roads> list = roadsRepository.findAll(pageable);
	}
    
}
