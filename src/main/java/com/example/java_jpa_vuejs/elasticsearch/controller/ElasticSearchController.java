package com.example.java_jpa_vuejs.elasticsearch.controller;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.net.URLDecoder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_jpa_vuejs.common.model.PaginationDto;
import com.example.java_jpa_vuejs.common.repositoryService.PaginationFirebaseService;
import com.example.java_jpa_vuejs.common.utility.PaginationAsyncCloud;
import com.example.java_jpa_vuejs.common.utility.PaginationAsyncPageable;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.geomBoard.model.BoardDto;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardFirebaseService;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ElasticSearchController {

    final private static Logger LOG = Logger.getGlobal();

    public static final String SECURED_TEXT = "Hello from the secured resource!";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BoardService boardService;
    private final BoardFirebaseService boardFirebaseService;
    private final PaginationFirebaseService paginationFirebaseService;

    /**
    * @method 지오메트릭 데이터 가져오기
    * @param  null
    * @throws Exception
    */
    @GetMapping(value = {"/getSearchList"})
    public List<Map<String, Object>> getGeomBoard(@Valid BoardDto boardDTO) throws Exception {
        System.out.println("★★★★★★-----------★★                             ★★" + boardDTO.getBoardSq());

        List<Map<String, Object>> list = null;
        //List<Map<String, Object>> list = boardFirebaseService.getGeomData(Integer.parseInt(boardDTO.getBoardSq())); 
        //List<Map<String, Object>> list2 = boardFirebaseService.getGeomData(Integer.parseInt(boardDTO.getBoardSq()));
  
        return list;
    }
}