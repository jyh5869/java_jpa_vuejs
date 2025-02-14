package com.example.java_jpa_vuejs.geomBoard.repositoryService;

import com.example.java_jpa_vuejs.common.model.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;

public interface BoardService {

     Iterable<GeometryBoard> getGeomBoardListAll();

     Iterable<GeometryBoard> getGeomBoardList(PaginationDto paginationDto);
     
     Integer getTotalCount();
}
