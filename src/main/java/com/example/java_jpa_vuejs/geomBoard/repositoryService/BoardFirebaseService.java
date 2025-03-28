package com.example.java_jpa_vuejs.geomBoard.repositoryService;

import java.util.List;
import java.util.Map;

import com.example.java_jpa_vuejs.common.model.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.model.BoardDto;

import jakarta.validation.Valid;

public interface BoardFirebaseService {

	void setGeomdData(long id, String geomPolygons) throws Exception;

	void setBoardData(long id, BoardDto boardDTO) throws Exception;

	void setDeleteGeomData(String boardSq) throws Exception;

    void setDeleteBoardData(String boardSq) throws Exception;

	void deleteGeomdData(String[] geomDeleteArr) throws Exception;

	Integer getTotalCount(PaginationDto paginationDto) throws Exception;
		
    long getLastIndex() throws Exception;

	List<Map<String, Object>> getGeomData(long id) throws Exception;

	List<Map<String, Object>> getGeomBoardList(PaginationDto paginationDto) throws Exception;

	List<Map<String, Object>> getGeomBoardListAll() throws Exception;
}
