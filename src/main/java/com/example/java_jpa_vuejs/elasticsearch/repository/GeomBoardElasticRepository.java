package com.example.java_jpa_vuejs.elasticsearch.repository;

import com.example.java_jpa_vuejs.elasticsearch.document.AlcoholDocument;
import com.example.java_jpa_vuejs.elasticsearch.document.GeomBoardDocument;

import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GeomBoardElasticRepository extends ElasticsearchRepository<GeomBoardDocument, String> , GeomBoardElasticRepositoryCustom {
    
    SearchHits<GeomBoardDocument> findByTitle(String title);

    SearchHits<GeomBoardDocument> findAllBy();
}