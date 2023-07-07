package com.example.java_jpa_vuejs.geomBoard.repositoryService;

import java.util.List;
import java.util.Map;

import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.geomBoard.BoardDto;

import jakarta.validation.Valid;

public interface BoardFirebaseService {

	void setGeomdData(long id, String geomPolygons) throws Exception;

	List<Map<String, Object>> getGeomData(long id) throws Exception;

}
