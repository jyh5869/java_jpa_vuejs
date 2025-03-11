package com.example.java_jpa_vuejs.elasticsearch.service;

import com.example.java_jpa_vuejs.elasticsearch.document.GeomBoardDocument;
import com.example.java_jpa_vuejs.elasticsearch.repository.GeomBoardElasticRepository;
import com.example.java_jpa_vuejs.elasticsearch.repository.GeomBoardElasticRepositoryCustom;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeomBoardSearchService {
 
    private final GeomBoardElasticRepository geomBoardElasticRepository;

    public SearchHits<GeomBoardDocument> search(String keyword) {
        return geomBoardElasticRepository.searchByKeyword(keyword);
    }

}
