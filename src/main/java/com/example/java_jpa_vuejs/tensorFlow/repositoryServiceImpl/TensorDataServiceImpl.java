package com.example.java_jpa_vuejs.tensorFlow.repositoryServiceImpl;

import org.springframework.stereotype.Service;

import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;
import com.example.java_jpa_vuejs.tensorFlow.repositoryJPA.RoadsRepository;
import com.example.java_jpa_vuejs.tensorFlow.repositoryService.TensorDataService;

import lombok.RequiredArgsConstructor;


@Service("tensorDataService")
@RequiredArgsConstructor
public class TensorDataServiceImpl implements TensorDataService {
	
	private final RoadsRepository roadsRepository;
	/*
	 * 도로명 데이터 가져오기
	 */
	public Iterable<Roads> getAddrData() {
		

		//Pageable pageable =  PageRequest.of(0, 100);//페이징 객체 선언
		//Page<Roads> list = roadsRepository.findAll(pageable);

		Iterable<Roads> list = roadsRepository.findAll();

		//Iterable<Roads> list2 = roadsRepository.getAllRoads();

		return list;
	}
    
	
}
