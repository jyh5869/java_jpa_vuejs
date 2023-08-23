package com.example.java_jpa_vuejs.geomBoard.repositoryServiceImpl;

import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.auth.entity.Phones;
import com.example.java_jpa_vuejs.auth.repositoryJPA.MemberRepository;
import com.example.java_jpa_vuejs.auth.repositoryJPA.PhonesRepository;
import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.geomBoard.repositoryJPA.GeometryBoardRepository;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardService;
import com.example.java_jpa_vuejs.util.DuplicatedException;
import com.example.java_jpa_vuejs.util.ForbiddenException;
import com.example.java_jpa_vuejs.util.UserNotFoundException;
import com.example.java_jpa_vuejs.validation.Empty;

import org.springframework.stereotype.Service;

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
	public Iterable<GeometryBoard> getGeomBoardList() {
		//DTO -> Entity   
		//Members loginEntity = loginDto.toEntity();
		 
		geometryBoardRepository.findAll();

		//아이디 중복 체크 결과 리턴
		return geometryBoardRepository.findAll();
	}
}