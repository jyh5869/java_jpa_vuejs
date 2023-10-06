package com.example.java_jpa_vuejs.controller;

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

import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.common.repositoryService.PaginationFirebaseService;
import com.example.java_jpa_vuejs.geomBoard.BoardDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardFirebaseService;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardService;
import com.example.java_jpa_vuejs.util.PaginationAsyncCloud;
import com.example.java_jpa_vuejs.util.PaginationAsyncPageable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GeomBoardController {

    final private static Logger LOG = Logger.getGlobal();

    public static final String SECURED_TEXT = "Hello from the secured resource!";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BoardService boardService;
    private final BoardFirebaseService boardFirebaseService;
    private final PaginationFirebaseService paginationFirebaseService;


    /**
    * @method 지오메트릭 데이터 저장
    * @param  null
    * @throws Exception
    */
    @GetMapping(value = {"/setGeomBoard"})
    public String setGeomBoard(@Valid BoardDto boardDTO) throws Exception {

        System.out.println("---------SET GEOMETRY BOARD PARAMS-----------");
        System.out.println("getActionType - " + boardDTO.getActionType());
        System.out.println("getId         - " + boardDTO.getId());
        System.out.println("getDocId      - " + boardDTO.getDocId());
        System.out.println("getBoardSq    - " + boardDTO.getBoardSq());
        System.out.println("getUserEmail  - " + boardDTO.getUserEmail());
        System.out.println("getUserNm     - " + boardDTO.getUserNm());
        System.out.println("getUserAdress - " + boardDTO.getUserAdress());
        System.out.println("getTitle      - " + boardDTO.getTitle());
        System.out.println("getContents1  - " + boardDTO.getContents1());
        System.out.println("getContents2  - " + boardDTO.getContents2());
        System.out.println("getState      - " + boardDTO.getState());
        System.out.println("getZipCd      - " + boardDTO.getZipCd());
        System.out.println("getUseYn      - " + boardDTO.getUseYn());
        System.out.println("--------------------------------------------");
        
        if(boardDTO.getActionType().equals("insert")){
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
    * @param  paginationDto 페이징 정보 Object
    * @throws Exception
    */
    @PostMapping(value = {"/getGeomBoardList"})//Pageable pageable
    public Map<String, Object> getGeomBoardList(@Valid PaginationDto paginationDto) throws Exception {
        //응답 리스트 객체 정의
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
        String pagination = "";

        try {
            // 1] - MYSQL에서 정보조회
            LOG.info(" DB AUTH ERROR - CLOUD AUTH START!");

            paginationDto.setCurrentPage(URLDecoder.decode(paginationDto.getCurrentPage().trim(), "UTF-8"));
            paginationDto.setCollectionNm("geometry_board");
            paginationDto.setOrderbyCol("reg_dt");

            //paginationDto.setTotalCount(boardFirebaseService.getTotalCount(paginationDto));
            paginationDto.setTotalCount(paginationFirebaseService.getTotalCount(paginationDto));
            paginationDto.setFirstDoc(paginationFirebaseService.getFirstDoc(paginationDto));
            paginationDto.setPrevDoc(paginationFirebaseService.getPrevDoc(paginationDto));
            paginationDto.setLastDoc(paginationFirebaseService.getLastDoc(paginationDto));
            paginationDto.setDocIdArr(paginationFirebaseService.getDocIdList(paginationDto));
            paginationDto.setNextDoc(paginationFirebaseService.getNextDoc(paginationDto));

            pagination = PaginationAsyncCloud.getDividePageFormByParams(paginationDto);
            
            retMap.put("dorIdArr", String.valueOf(paginationDto.getDocIdArr()));

            retList = boardFirebaseService.getGeomBoardList(paginationDto); 
        } 
        catch (Exception e) {
            /* mYSQL과 FIREBASE의 순환어떻게 시킬까??
            
            // 2] - MYSQL에서 정보조회 실패시 FIREBASE로 조회
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

            pagination = PaginationAsyncPageable.getDividePageFormByParams(paginationDto);
            
            e.printStackTrace();
            */
        }

        retMap.put("list", retList);
        retMap.put("pagination", pagination);

        return retMap;
    }
}