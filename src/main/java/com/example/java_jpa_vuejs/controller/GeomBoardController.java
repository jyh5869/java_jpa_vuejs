package com.example.java_jpa_vuejs.controller;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;
import com.example.java_jpa_vuejs.geomBoard.BoardDto;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardFirebaseService;
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
    private final BoardFirebaseService boardFirebaseService;

    /**
    * @method 지오메트릭 데이터 저장
    * @param  null
    * @throws Exception
    */
    @GetMapping(value = {"/setGeomBoard"})
    public String setGeomBoard(@Valid BoardDto boardDTO) throws Exception {
        
        String dataType = boardDTO.getGeomPolygons().getClass().getName();
        String geomPolygon = URLDecoder.decode(boardDTO.getGeomPolygons().toString(), "UTF-8");
        String geomCircle = URLDecoder.decode(boardDTO.getGeomCircles().toString(), "UTF-8");
        

        System.out.println("☆ ☆ ☆ --------------------------");
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
        

        boardDTO.setId(boardFirebaseService.getLastIndex());

        boardFirebaseService.setGeomdData(boardDTO.getId(), boardDTO.getGeomPolygons() );
        boardFirebaseService.setGeomdData(boardDTO.getId(), boardDTO.getGeomLineStrings());
        boardFirebaseService.setGeomdData(boardDTO.getId(), boardDTO.getGeomPoints());
        boardFirebaseService.setGeomdData(boardDTO.getId(), boardDTO.getGeomCircles());

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

        List<Map<String, Object>> list = boardFirebaseService.getGeomData(boardDTO.getId());

        return list;
    }

    /**
    * @method 지오메트릭 글 리스트 가져오기
    * @param  null
    * @throws Exception
    */
    @GetMapping(value = {"/getGeomBoardList"})
    public List<Map<String, Object>> getGeomBoardList(@Valid BoardDto boardDTO) throws Exception {

        List<Map<String, Object>> list = boardFirebaseService.getGeomBoardList();

        return list;
    }
}