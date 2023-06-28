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
import com.example.java_jpa_vuejs.geomBoard.GeomBoardDTO;
import com.example.java_jpa_vuejs.geomBoard.GeomBoardDto;
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

    /**
    * @method 지오메트릭 데이터 저장
    * @param  null
    * @throws Exception
    */
    @GetMapping(value = {"/setGeomBoard"})
    public String setGeomBoard(@Valid GeomBoardDto geomBoardDTO) throws Exception {
        
        String decodeData = geomBoardDTO.getArrpolygon().getClass().getName();
        String decodeData1 = URLDecoder.decode(geomBoardDTO.getArrpolygon().toString(), "UTF-8");
        

        System.out.println("☆ ☆ ☆ --------------------------");
        System.out.println(decodeData1);
        System.out.println(decodeData);
        //System.out.println(geomBoardDTO.getPoint());
        System.out.println(geomBoardDTO.getArrpolygon());
        System.out.println("☆ ☆ ☆ --------------------------");

        //GeoJSONReader reader1 = new GeoJSONReader();
        //Geometry geometry = reader1.read(content);

        return "TRUE";
    }
}