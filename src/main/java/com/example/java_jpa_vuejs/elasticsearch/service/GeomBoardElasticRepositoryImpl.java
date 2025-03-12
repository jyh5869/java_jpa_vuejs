package com.example.java_jpa_vuejs.elasticsearch.service;

import com.example.java_jpa_vuejs.elasticsearch.document.GeomBoardDocument;
import com.example.java_jpa_vuejs.elasticsearch.repository.GeomBoardElasticRepositoryCustom;

import lombok.RequiredArgsConstructor;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
//import org.springframework.data.elasticsearch.core.query.NativeQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;  // ★ 추가된 import
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeomBoardElasticRepositoryImpl implements GeomBoardElasticRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchHits<GeomBoardDocument> searchByKeyword(String keyword) {
        System.out.println("통합 쿼리로 검색 할게");
        // MultiMatchQuery를 활용하여 검색 쿼리 생성
        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .query(keyword)
                                .fields("title", "contents1", "contents2", "userName", "userEmail", "userAddress", "zipCode", "geometry")
                                .type(TextQueryType.BestFields)  // ★ 변경된 부분 // 일치도가 높은 필드를 우선
                        )
                )
                .build();

                System.out.println(searchQuery.toString());
                
                System.out.println("----------------");
        // ElasticsearchOperations를 통해 검색 실행
        return elasticsearchOperations.search(searchQuery, GeomBoardDocument.class);
    }
}
    