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

    private Integer blockPage = 2;//한번에 몇개의 페이징 정보를 제공할것인지 ex> 1,2,3,4,5,6,7,8,9,10 페이지

    private Integer countPerPage = 2;//한페이지에 몇개의 개시물을 보여줄것인지 ex> 10개

    private String callType;

    private String actionTarget;// 1.동기 - 컨트롤러 명  2.비동기 - 컨트롤러 호출 함수명

    private String params;

    private String resType = "";
}
