package com.example.java_jpa_vuejs.common;

import java.net.URLDecoder;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class PaginationDto {
    
    //1. MySQL 페이징 테스트 변수 1
    //private Integer blockPage =2;
    //private Integer countPerPage = 4;

    //1. MySQL 페이징 테스트 변수 2
    //private Integer blockPage =3;
    //private Integer countPerPage = 2;

    private Integer blockPage = 2;// 한번에 제공할 페이징 이미지 수  ex> 1,2,3,4,5,6,7,8,9,10 페이지

    private Integer countPerPage = 2;// 한페이지당 보여줄 게시물 수  ex> 10개

    private Integer totalCount = 0;// 리스트의 총 갯수

    private String currentPage = "0";// 호출된 현재 페이지

    private String callType;// 호출 타입(필요시 사용)

    private String actionTarget;// 1.동기 - 컨트롤러 명  2.비동기 - 컨트롤러 호출 함수명

    private String params = "";// 조건 파라메터






    private String docIdArr;//페이징을 위한 클라우드 도큐먼트 ID ARR

    private String lastDoc;

    private String firstDoc;

    private String nextDoc;

    private String pageGroupFirst;

    private String prevDoc;

    public String getDocIdArr(){
        return URLDecoder.decode(docIdArr);
    }

    /*
    public String setDocIdArr(){
        return URLDecoder.decode(docIdArr);
    }
    */

    
}
