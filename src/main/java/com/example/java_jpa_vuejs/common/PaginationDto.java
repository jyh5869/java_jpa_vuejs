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
    
    private Integer totalCount = 0;

    private Integer currentPage = 0;

    //private Integer blockPage = 10;

    private Integer countPerPage = 10;

    private Integer resultCnt = 10;

    private String callType;

    private String actionTarget;// 1.동기 - 컨트롤러 명  2.비동기 - 컨트롤러 호출 함수명

    private String params;
    
    private String resType = "";
}
