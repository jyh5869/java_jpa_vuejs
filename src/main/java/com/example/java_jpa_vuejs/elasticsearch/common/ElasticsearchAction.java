package com.example.java_jpa_vuejs.elasticsearch.common;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ElasticsearchAction {
    private final ElasticsearchOperations elasticsearchOperations;

    /* 
        index 생성 함수
        index 생성 확인: curl -X GET "http://localhost:9200/geometry_board"
    */
    @PostConstruct
    public void createIndex() {
        String indexName = "geometry_board";
        IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);

        // ✅ 1. IndexOperations 객체 가져오기
        IndexOperations indexOps = elasticsearchOperations.indexOps(indexCoordinates);

        try {
            // ✅ 2. 기존 색인이 존재하면 삭제
            if (indexOps.exists()) {
                System.out.println("Existing index found. Deleting: " + indexName);
                indexOps.delete();
            }
        } catch (Exception e) {
            System.err.println("Error checking/deleting index: " + e.getMessage());
            return; // 오류 발생 시 중단
        }

        try {
            // ✅ 3. 매핑 정보 설정
            Map<String, Object> settings = new HashMap<>();
            settings.put("index.number_of_shards", 1);
            settings.put("index.number_of_replicas", 0);

            Map<String, Object> properties = new HashMap<>();
            properties.put("id", Map.of("type", "keyword"));
            properties.put("boardSq", Map.of("type", "long"));
            properties.put("title", Map.of("type", "text", "analyzer", "nori"));
            properties.put("contents1", Map.of("type", "text", "analyzer", "nori"));
            properties.put("contents2", Map.of("type", "text", "analyzer", "nori"));
            properties.put("userName", Map.of("type", "keyword"));
            properties.put("userEmail", Map.of("type", "keyword"));
            properties.put("userAddress", Map.of("type", "keyword"));
            properties.put("zipCode", Map.of("type", "keyword"));
            properties.put("useYn", Map.of("type", "boolean"));
            //properties.put("regDt", Map.of("type", "date"));
            // regDt에 대해 포맷 지정
            properties.put("regDt", Map.of(
                "type", "date",
                "format", "yyyy-MM-dd HH:mm:ss"
            ));

            properties.put("geometry", Map.of("type", "object"));

            Map<String, Object> mappings = new HashMap<>();
            mappings.put("properties", properties);  // ✅ 여기서 properties를 감싸야 함

            // ✅ 4. 인덱스 생성
            Document indexConfig = Document.create();
            indexConfig.put("settings", settings);
            indexConfig.put("mappings", mappings);  // ✅ 올바른 구조 반영

            indexOps.create(indexConfig);
            System.out.println("Elasticsearch index created: " + indexName);
        } catch (Exception e) {
            System.err.println("Error creating index: " + e.getMessage());
        }
    }
}
