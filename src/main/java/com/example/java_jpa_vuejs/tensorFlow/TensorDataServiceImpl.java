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
	 * 도로명 데이터 가져오기
	 */
	public Iterable<Roads> getAddrData() {
		

		//Pageable pageable =  PageRequest.of(0, 100);//페이징 객체 선언
		//Page<Roads> list = roadsRepository.findAll(pageable);

		//Iterable<Roads> list = roadsRepository.findAll();

		Iterable<Roads> list2 = roadsRepository.getAllRoads();

		return list2;
	}
    
}
