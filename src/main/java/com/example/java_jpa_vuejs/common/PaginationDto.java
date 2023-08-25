package com.example.java_jpa_vuejs.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class PaginationDto {
    
    private long totalCount = 0;

    private long currentPage = 0;

    private long blockPage = 10;

    private long resultCnt = 10;

    private String callType;
}
