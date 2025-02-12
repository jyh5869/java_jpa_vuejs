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
import com.example.java_jpa_vuejs.elasticsearch.document.AlcSearchKeyDocument;
import com.example.java_jpa_vuejs.elasticsearch.document.AlcoholDocument;
import com.example.java_jpa_vuejs.elasticsearch.document.TagDocument;
import com.example.java_jpa_vuejs.elasticsearch.repository.AlcoholElasticsearchRepository;
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

    //private final alcoholRepository alcoholRepository;
    private final AlcoholElasticsearchRepository alcoholElasticsearchRepository;

    /**
    * @method 지오메트릭 데이터 가져오기
    * @param  null
    * @throws Exception
    */
    @GetMapping(value = {"/getSearchList2322"})
    public List<Map<String, Object>> getGeomBoard(@Valid BoardDto boardDTO) throws Exception {
        System.out.println("★★★★★★-----------★★                             ★★" + boardDTO.getBoardSq());

        List<Map<String, Object>> list = null;
        //List<Map<String, Object>> list = boardFirebaseService.getGeomData(Integer.parseInt(boardDTO.getBoardSq())); 
        //List<Map<String, Object>> list2 = boardFirebaseService.getGeomData(Integer.parseInt(boardDTO.getBoardSq()));
  
        return list;
    }

     /**
     * 데이터베이스 속 alcohol 데이터 전부 elasticsearch에 인덱싱
     */
    @GetMapping(value = {"/setSearchData"})
    public void indexAll(@Valid BoardDto boardDTO) {
        //List<Alcohol> alcohols = alcoholRepository.findAllWithSearchKeys();
        List<Map<String, Object>> list;
        try {
            list = boardFirebaseService.getGeomData(Integer.parseInt(boardDTO.getBoardSq()));

            // alcohol 리스트를 하나씩 처리
            for (Map<String, Object> alcohol : list) {
                // alcohol 엔티티에서 필요한 값들을 하나씩 꺼내서 AlcoholDocument 객체에 설정
                AlcoholDocument alcoholDocument = new AlcoholDocument();
                
                // Alcohol 엔티티에서 값을 하나씩 가져와서 AlcoholDocument에 설정
                alcoholDocument.setCategory((String) alcohol.get("title"));
                alcoholDocument.setContents((String) alcohol.get("dfdfsdfsdf"));
                
                //alcSearchKeyDocuments.add(alcSearchKeyDocument);

                // AlcoholDocument에 Tags와 SearchKeys도 각각 설정 (필요한 경우)
                //List<TagDocument> tagDocuments = new ArrayList<>();
                /* 
                for (Tag tag : alcohol.getTags()) {
                    TagDocument tagDocument = new TagDocument();
                    tagDocument.setTagName(tag.getTagName());
                    tagDocument.setTagValue(tag.getTagValue());
                    tagDocuments.add(tagDocument);
                }
                alcoholDocument.setTags(tagDocuments);

                List<AlcSearchKeyDocument> alcSearchKeyDocuments = new ArrayList<>();
                for (AlcSearchKey alcSearchKey : alcohol.getSearchKeys()) {
                    AlcSearchKeyDocument alcSearchKeyDocument = new AlcSearchKeyDocument();
                    alcSearchKeyDocument.setKeyName(alcSearchKey.getKeyName());
                    alcSearchKeyDocument.setKeyValue(alcSearchKey.getKeyValue());
                    alcSearchKeyDocuments.add(alcSearchKeyDocument);
                }
                alcoholDocument.setSearchKeys(alcSearchKeyDocuments);
                */
                // 하나씩 인덱싱
                alcoholElasticsearchRepository.save(alcoholDocument);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
    }
}