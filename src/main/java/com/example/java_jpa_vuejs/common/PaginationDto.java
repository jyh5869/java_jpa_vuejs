package com.example.java_jpa_vuejs.common;

import java.net.URLDecoder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class PaginationDto {
    
    /* 공통 페이지 처리 변수 */
    private Integer blockPage = 2;    // 한번에 제공할 페이징 이미지 수  ex> 1,2,3,4,5,6,7,8,9,10 페이지

    private Integer countPerPage = 2; // 한페이지당 보여줄 게시물 수  ex> 10개

    private Integer totalCount = 0;   // 불러올 리스트의 총 갯수

    private String currentPage = "0"; // 호출된 현재 페이지

    private String params;            // 조건 파라메터

    private String actionTarget;      // 1.동기 - 컨트롤러 명  2.비동기 - 컨트롤러 호출 함수명

    private String callType;          // 호출 타입(필요시 사용)

    /* 클라우드(파이어베이스) 페이지처리를 위한 변수 */
    private String collectionNm;      // 컬렉션 명

    private String orderbyCol;        // 컬렉션 정렬 기준 컬럼

    private String docIdArr;          // 페이징을 위한 클라우드 도큐먼트 ID ARRAY

    private String firstDoc;          // 페이지 그룹의 맨처음 페이지

    private String prevDoc;           // 패아자 그룹의 이전 페이지

    private String nextDoc;           // 페이지 그룹의 다음 페이지

    private String lastDoc;           // 페이지 그룹의 마지막 페이지
    
    /* Getter, Setter */
    public String getDocIdArr(){
        return URLDecoder.decode(docIdArr);
    }
}
