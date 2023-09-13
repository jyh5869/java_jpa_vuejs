package com.example.java_jpa_vuejs.geomBoard.repositoryServiceImpl;

import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.geomBoard.repositoryJPA.GeometryBoardRepository;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;

@Service("boardService")
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final ModelMapper modelMapper;
	private final GeometryBoardRepository geometryBoardRepository;
	
	@Override
	public Iterable<GeometryBoard> getGeomBoardList(PaginationDto paginationDto) {

		Integer intCurrentPage = Integer.valueOf(paginationDto.getCurrentPage());
		Integer intCountPerPage = Integer.valueOf(paginationDto.getCountPerPage());
		
		//페이징 호출
		Pageable pageable =  PageRequest.of(intCurrentPage, intCountPerPage);//페이징 객체 선언

		Page<GeometryBoard> list = geometryBoardRepository.findAll(pageable);
		
		return list;
	}


	@Override
	public Integer getTotalCount() {
		long longTotlCount = geometryBoardRepository.count();

		Integer totalCount = Long.valueOf(longTotlCount).intValue();

		return totalCount;
	}
}