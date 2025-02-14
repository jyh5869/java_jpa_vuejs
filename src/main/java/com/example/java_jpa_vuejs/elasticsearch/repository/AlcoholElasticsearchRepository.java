package com.example.java_jpa_vuejs.elasticsearch.repository;

import com.example.java_jpa_vuejs.elasticsearch.document.AlcoholDocument;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AlcoholElasticsearchRepository extends ElasticsearchRepository<AlcoholDocument, String> {
    
    SearchHits<AlcoholDocument> findByTitle(String title);

    SearchHits<AlcoholDocument> findAllBy();
}