package com.example.java_jpa_vuejs.geomBoard.repositoryService;

import java.util.List;
import java.util.Map;

import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.BoardDto;

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

	List<Map<String, Object>> getGeomBoardList2(PaginationDto paginationDto) throws Exception;



	
	String getDocIdList(PaginationDto paginationDto) throws Exception;

	String getFirstDoc(PaginationDto paginationDto) throws Exception;

	String getNextDoc(PaginationDto paginationDto) throws Exception;

    String getLastDoc(PaginationDto paginationDto) throws Exception;

	String getPrevDoc(PaginationDto paginationDto) throws Exception;
}
