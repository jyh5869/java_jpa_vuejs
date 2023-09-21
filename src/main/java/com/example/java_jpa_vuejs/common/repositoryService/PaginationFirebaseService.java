package com.example.java_jpa_vuejs.common.repositoryService;

import com.example.java_jpa_vuejs.common.PaginationDto;

public interface PaginationFirebaseService {
    
    String getDocIdList(PaginationDto paginationDto) throws Exception;

	String getFirstDoc(PaginationDto paginationDto) throws Exception;

	String getPrevDoc(PaginationDto paginationDto) throws Exception;

	String getNextDoc(PaginationDto paginationDto) throws Exception;

    String getLastDoc(PaginationDto paginationDto) throws Exception;
}
