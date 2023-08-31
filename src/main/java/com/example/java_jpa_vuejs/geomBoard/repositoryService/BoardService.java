package com.example.java_jpa_vuejs.geomBoard.repositoryService;

import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;

import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;

public interface BoardService {

     Iterable<GeometryBoard> getGeomBoardList(PaginationDto paginationDto);
     Integer getTotalCount();
}
