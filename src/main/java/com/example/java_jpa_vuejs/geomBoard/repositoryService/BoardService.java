package com.example.java_jpa_vuejs.geomBoard.repositoryService;

import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;

public interface BoardService {

     Iterable<GeometryBoard> getGeomBoardList(PaginationDto paginationDto);

     Integer getTotalCount();
}
