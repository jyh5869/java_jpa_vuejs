package com.example.java_jpa_vuejs.elasticsearch.repository;

import com.example.java_jpa_vuejs.elasticsearch.document.GeomBoardDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface GeomBoardElasticRepositoryCustom {
    SearchHits<GeomBoardDocument> searchByKeyword(String keyword);
}