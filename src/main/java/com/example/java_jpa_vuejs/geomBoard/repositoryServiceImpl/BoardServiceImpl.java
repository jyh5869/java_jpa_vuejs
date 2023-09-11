package com.example.java_jpa_vuejs.geomBoard.repositoryServiceImpl;

import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.auth.entity.Phones;
import com.example.java_jpa_vuejs.auth.repositoryJPA.MemberRepository;
import com.example.java_jpa_vuejs.auth.repositoryJPA.PhonesRepository;
import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.geomBoard.repositoryJPA.GeometryBoardRepository;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardService;
import com.example.java_jpa_vuejs.util.DuplicatedException;
import com.example.java_jpa_vuejs.util.ForbiddenException;
import com.example.java_jpa_vuejs.util.UserNotFoundException;
import com.example.java_jpa_vuejs.validation.Empty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;


@Service("boardService")
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final ModelMapper modelMapper;
	private final GeometryBoardRepository geometryBoardRepository;
	

	@Override
	public Iterable<GeometryBoard> getGeomBoardList(PaginationDto paginationDto) {

		Integer intCurrentPage = paginationDto.getCurrentPage();
		Integer intCountPerPage = paginationDto.getCountPerPage();
		
		//페이징 호출
		Pageable pageable =  PageRequest.of(-1, intCountPerPage);//페이징 객체 선언
		
		/* 
		if(callType.equals("first")){
			pageable = PageRequest.of(0, intCountPerPage);
		}
		else if(callType.equals("middle")){
			pageable = PageRequest.of(intCountPerPage, intCountPerPage);
		}
		else if(callType.equals("last")){
			pageable = PageRequest.of(intCountPerPage, intCountPerPage);
		}
		*/

		Page<GeometryBoard> list = geometryBoardRepository.findAll(pageable);
		
		return list;
	}


	@Override
	public Integer getTotalCount() {
		long longTotlCount = geometryBoardRepository.count();

		Integer totalCount = Long.valueOf(longTotlCount).intValue();

		System.out.println("★------------------------------------> totalCOunt : " + totalCount);
		//아이디 중복 체크 결과 리턴
		return totalCount;
	}
}