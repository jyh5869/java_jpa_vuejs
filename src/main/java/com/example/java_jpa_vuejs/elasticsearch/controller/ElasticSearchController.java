package com.example.java_jpa_vuejs.elasticsearch.controller;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_jpa_vuejs.common.model.PaginationDto;
import com.example.java_jpa_vuejs.common.repositoryService.PaginationFirebaseService;
import com.example.java_jpa_vuejs.common.utility.PaginationAsyncCloud;
import com.example.java_jpa_vuejs.common.utility.PaginationAsyncPageable;
import com.example.java_jpa_vuejs.elasticsearch.common.ElasticsearchAction;
import com.example.java_jpa_vuejs.elasticsearch.configuration.ElasticsearchConfig;
import com.example.java_jpa_vuejs.elasticsearch.document.AlcSearchKeyDocument;
import com.example.java_jpa_vuejs.elasticsearch.document.AlcoholDocument;
import com.example.java_jpa_vuejs.elasticsearch.document.GeomBoardDocument;
import com.example.java_jpa_vuejs.elasticsearch.document.TagDocument;
import com.example.java_jpa_vuejs.elasticsearch.model.AlcoholDto;
import com.example.java_jpa_vuejs.elasticsearch.model.SearchDto;
import com.example.java_jpa_vuejs.elasticsearch.repository.AlcoholElasticsearchRepository;
import com.example.java_jpa_vuejs.elasticsearch.repository.GeomBoardElasticRepository;
import com.example.java_jpa_vuejs.elasticsearch.service.GeomBoardSearchService;
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
    private final GeomBoardElasticRepository geomBoardElasticRepository;
    private final ElasticsearchAction elasticsearchAction;
    // private final GeomBoardSearchService geomBoardSearchService;

    /**
     * title 검색어를 활용해 title 필드 타겟으로 elasticsearch에 검색 쿼리
     * @param title title 필드 타겟의 검색어
     * @return responseBody에 바로 적재할 수 있는 형태의 Map 객체
     */
    @GetMapping(value = {"/search/getSearchData"})
    public Map<String, Object> searchTitle(String keyword) {
        System.out.println("검색 할게 ☆ searchKeyword :  " + keyword);
        // elasticsearch 검색
        //SearchHits<GeomBoardDocument> searchHits = geomBoardElasticRepository.findAllBy();
        //SearchHits<GeomBoardDocument> searchHits = geomBoardElasticRepository.findByTitle(keyword);
        //SearchHits<AlcoholDocument> searchHits = alcoholElasticsearchRepository.findAllBy();
        SearchHits<GeomBoardDocument> searchHits = geomBoardElasticRepository.searchByKeyword(keyword);
        // 리턴할 결과 Map 객체
        
        Map<String, Object> result = new HashMap<>();

        // 결과 개수
        result.put("count", searchHits.getTotalHits());

        System.out.println(searchHits.getTotalHits());
        System.out.println(searchHits);
        System.out.println("포문 반복! 준비완료");
        // 결과 컨텐츠
        List<GeomBoardDocument> alcoholDTOList = new ArrayList<>();
        for(SearchHit<GeomBoardDocument> hit : searchHits) {
            System.out.println("포문 반복!");
            GeomBoardDocument geomBoardDocument = hit.getContent(); // SearchHit에서 AlcoholDocument 객체를 가져옴

            // AlcoholDocument에서 AlcoholDTO로 데이터 변환
            
            // 변환된 AlcoholDTO를 리스트에 추가
            alcoholDTOList.add(geomBoardDocument);
        }

        result.put("data", alcoholDTOList);

        return result;
    }

    
     /**
     * 데이터베이스 속 alcohol 데이터 전부 elasticsearch에 인덱싱
     */
    @GetMapping(value = {"/search/setMakeSearchData"})
    public void indexAll(@Valid SearchDto searchDto ) {
        String searchType = searchDto.getSearchType();

        try {
            //파이어베이스 데이터 색인
            if(searchType.equals("firebase")){

                elasticsearchAction.createIndex();
                
                List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
                retList = boardFirebaseService.getGeomBoardListAll(); 

                GeomBoardDocument geomBoardDocument = new GeomBoardDocument();

                // 순회하며 값 조회
                for (Map<String, Object> map : retList) {
                    
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }

                    geomBoardDocument.setId((String) map.get("docId"));
                    geomBoardDocument.setBoardSq((Long) Long.parseLong((String) map.get("board_sq")));
                    geomBoardDocument.setTitle((String) map.get("title"));
                    geomBoardDocument.setContents1((String) map.get("contents1"));
                    geomBoardDocument.setContents2((String) map.get("contents2"));
                    geomBoardDocument.setGeometry((Map<String, Object>) map.get("geometry"));
                    geomBoardDocument.setUserAddress((String) map.get("user_address"));
                    geomBoardDocument.setUserEmail((String) map.get("user_email"));
                    geomBoardDocument.setUserName((String) map.get("user_name"));
                    geomBoardDocument.setZipCode((String) map.get("zip_code"));
                    geomBoardDocument.setUseYn((Boolean) map.get("stats"));

                    //geomBoardDocument.setRegDt((String) map.get("reg_dt"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 시간 형식 확인 필요
                    //geomBoardDocument.setRegDt(LocalDateTime.parse((String) map.get("reg_dt"), formatter));
                    
                    geomBoardDocument.setRegDt((String) map.get("reg_dt"));
                    System.out.println("★★★★        ★★★                " + geomBoardDocument.getRegDt());
                    
                    Object useYnObj = map.get("use_yn");
                    if (useYnObj instanceof Boolean) {
                        geomBoardDocument.setUseYn((Boolean) useYnObj);
                    } else if (useYnObj instanceof String) {
                        geomBoardDocument.setUseYn(Boolean.parseBoolean((String) useYnObj));
                    } else {
                        geomBoardDocument.setUseYn(false); // 기본값 설정
                    }

                    System.out.println("----------------");

                    geomBoardElasticRepository.save(geomBoardDocument);
                }


            }//mysql 데이터 색인
            else{
                Iterable<GeometryBoard> list = boardService.getGeomBoardListAll();
                Iterator<GeometryBoard> itr = list.iterator();
                
                System.out.println(list.toString());

                if (!list.iterator().hasNext()) {
                    System.out.println("No data found.");
                } else {
                    // Iterable을 for-each로 순회하면서 데이터 출력
                    for (GeometryBoard geometryBoard : list) {
                        System.out.println("ID: " + geometryBoard.getTitle());
                        System.out.println("Table ID: " + geometryBoard.getContents1());
                        //System.out.println("Title: " + geometryBoard.getTitle());
                        //System.out.println("Contents: " + geometryBoard.getContents());
                        //System.out.println("Category: " + geometryBoard.getCategory());
        /*
                        // Tag들 출력
                        if (geometryBoard.getTags() != null && !geometryBoard.getTags().isEmpty()) {
                            for (TagDocument tag : geometryBoard.getTags()) {
                                System.out.println("Tag: " + tag.getTagName());
                            }
                        } else {
                            System.out.println("No tags found.");
                        }
        
                        // SearchKeys들 출력
                        if (geometryBoard.getSearchKeys() != null && !geometryBoard.getSearchKeys().isEmpty()) {
                            for (AlcSearchKeyDocument searchKey : geometryBoard.getSearchKeys()) {
                                System.out.println("Search Key: " + searchKey.getKey());
                            }
                        } else {
                            System.out.println("No search keys found.");
                        }
        */
                        System.out.println("------------------------------");
                    }
                }
                
                
                // alcohol 리스트를 하나씩 처리
                for (GeometryBoard alcohol : list) {
                    // alcohol 엔티티에서 필요한 값들을 하나씩 꺼내서 AlcoholDocument 객체에 설정
                    AlcoholDocument alcoholDocument = new AlcoholDocument();
                    
                    System.out.println(alcohol.getTitle()); 

                    // Alcohol 엔티티에서 값을 하나씩 가져와서 AlcoholDocument에 설정
                    alcoholDocument.setCategory((String) alcohol.getTitle());
                    alcoholDocument.setTitle((String) alcohol.getTitle());
                    alcoholDocument.setContents((String) alcohol.getContents1());

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
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
    }


    /**
     * title 검색어를 활용해 title 필드 타겟으로 elasticsearch에 검색 쿼리
     * @param title title 필드 타겟의 검색어
     * @return responseBody에 바로 적재할 수 있는 형태의 Map 객체
     */
    @GetMapping(value = {"/search/setDeleteSearchData"})
    public Map<String, Object> setDeleteSearchData(String indexName) {
        System.out.println("검색 색인 삭제할게 : 삭제할 색인:  " + indexName);
        // elasticsearch 검색

        boolean deleted = elasticsearchAction.deleteIndex(indexName);
        // 리턴할 결과 Map 객체
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("deleted", deleted);
        result.put("data", data);
    
        return result;
    }
}