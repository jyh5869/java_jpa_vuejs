package com.example.java_jpa_vuejs.controller;

import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.jaxb.SpringDataJaxb.PageRequestDto;
import org.springframework.data.geo.Polygon;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_jpa_vuejs.auth.AuthProvider;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.auth.repositoryService.SignFirebaseService;
import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.auth2.repositoryService.RepositoryService;
import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;
import com.example.java_jpa_vuejs.geomBoard.BoardDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardFirebaseService;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import com.google.gson.FieldNamingPolicy;
//import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

//import org.locationtech.jts.geom.GeometryFactory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GeomBoardController {

    final private static Logger LOG = Logger.getGlobal();

    public static final String SECURED_TEXT = "Hello from the secured resource!";

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BoardService boardService;
    private final BoardFirebaseService boardFirebaseService;


    /**
    * @method 지오메트릭 데이터 저장
    * @param  null
    * @throws Exception
    */
    @GetMapping(value = {"/setGeomBoard"})
    public String setGeomBoard(@Valid BoardDto boardDTO) throws Exception {

        System.out.println("☆ ☆ ☆ --------------------------");
        System.out.println(boardDTO.getActionType());
        System.out.println(boardDTO.getId());
        System.out.println(boardDTO.getDocId());
        System.out.println(boardDTO.getBoardSq());
        System.out.println(boardDTO.getUserEmail());
        System.out.println(boardDTO.getUserNm());
        System.out.println(boardDTO.getUserAdress());
        System.out.println(boardDTO.getTitle());
        System.out.println(boardDTO.getContents1());
        System.out.println(boardDTO.getContents2());
        System.out.println(boardDTO.getState());
        System.out.println(boardDTO.getZipCd());
        System.out.println(boardDTO.getUseYn());
        System.out.println("☆ ☆ ☆ --------------------------");
        
        if(boardDTO.getActionType().equals("insert")){
            //boardDTO.setId(boardFirebaseService.getLastIndex());
            boardDTO.setBoardSq(String.valueOf(boardFirebaseService.getLastIndex()));
        }
        else if(boardDTO.getActionType().equals("update")){
            boardDTO.setDocId(boardDTO.getDocId());
        }
        else if(boardDTO.getActionType().equals("delete")){
            
            String boardSq = boardDTO.getBoardSq();

            boardFirebaseService.setDeleteGeomData(boardSq);
            boardFirebaseService.setDeleteBoardData(boardSq);

            return "TRUE";
        }
        
        boardFirebaseService.setGeomdData(Integer.parseInt(boardDTO.getBoardSq()), boardDTO.getGeomPolygons() );
        boardFirebaseService.setGeomdData(Integer.parseInt(boardDTO.getBoardSq()), boardDTO.getGeomLineStrings());
        boardFirebaseService.setGeomdData(Integer.parseInt(boardDTO.getBoardSq()), boardDTO.getGeomPoints());
        boardFirebaseService.setGeomdData(Integer.parseInt(boardDTO.getBoardSq()), boardDTO.getGeomCircles());

        boardFirebaseService.setBoardData(boardDTO.getId(), boardDTO);

        //삭제할 배열이 있을경우 삭제
        if(boardDTO.getGeomDeleteArr().length != 0){
            boardFirebaseService.deleteGeomdData(boardDTO.getGeomDeleteArr());
        }

        return "TRUE";
    }


    /**
    * @method 지오메트릭 데이터 가져오기
    * @param  null
    * @throws Exception
    */
    @GetMapping(value = {"/getGeomBoard"})
    public List<Map<String, Object>> getGeomBoard(@Valid BoardDto boardDTO) throws Exception {

        List<Map<String, Object>> list = boardFirebaseService.getGeomData(Integer.parseInt(boardDTO.getBoardSq()));

        return list;
    }

    
    /**
    * @method 지오메트릭 글 리스트 가져오기
    * @param  null
    * @throws Exception
    */
    @GetMapping(value = {"/getGeomBoardList"})//Pageable pageable
    public List<Map<String, Object>> getGeomBoardList(@Valid PaginationDto paginationDto) throws Exception {
        //응답 리스트 객체 정의
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();

        try {

            // 1] - MYSQL에서 정보조회
            
            paginationDto.setTotalCount(boardService.getTotalCount());
            Iterable<GeometryBoard> list = boardService.getGeomBoardList(paginationDto);
            Iterator<GeometryBoard> itr = list.iterator();

            //리턴 리스트에 담기위한 형식변경
            while(itr.hasNext()){
            
                GeometryBoard GeometryBoardEntity = itr.next();
                BoardDto boardDto = GeometryBoardEntity.toDto(GeometryBoardEntity);

                Map<String, Object> map = objectMapper.convertValue(boardDto, new TypeReference<Map<String, Object>>() {});

                retList.add(map);
            }
            
        } catch (Exception e) {
            // 2] - MYSQL에서 정보조회 실패시 FIREBASE로 조회
            LOG.info(" DB AUTH ERROR - CLOUD AUTH START!");
            retList = boardFirebaseService.getGeomBoardList();

            e.printStackTrace();
        }

        return retList;
    }
}