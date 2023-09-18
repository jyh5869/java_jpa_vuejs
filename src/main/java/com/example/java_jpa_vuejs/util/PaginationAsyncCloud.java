package com.example.java_jpa_vuejs.util;

import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardFirebaseService;

import lombok.RequiredArgsConstructor;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class PaginationAsyncCloud {

	final private static Logger LOG = Logger.getLogger(PaginationAsyncPageable.class.getName());

    public static String getDividePageFormByParams(PaginationDto paginationDto) throws Exception{

		String docIdArr = paginationDto.getDocIdArr();

		

        String strTargetDoc = paginationDto.getCurrentPage().split("\\|")[0].trim();// 보여줄 현재 페이지
		String strFirstDoc = paginationDto.getFirstDoc().split("\\|")[0].trim();
		String strNextDoc = paginationDto.getNextDoc();

		String strLastDoc = paginationDto.getLastDoc();

		System.out.println("★-----------------------------------------> = " + paginationDto.getCurrentPage().split("\\|")[1]);
		Integer intCurrentPage = Integer.valueOf(paginationDto.getCurrentPage().split("\\|")[1].trim());
		Integer intCountPerPage = paginationDto.getCountPerPage();// 한페이지당 보여질 게시물 수
		Integer intPageGroupSize = paginationDto.getBlockPage();// 패아장을 제공할 페이지 블록 수
		Integer intTotalCount = paginationDto.getTotalCount();// 총 게시물 갯수

		String strParams = paginationDto.getParams();// 조건 파라메터
		String strActionUrl = paginationDto.getActionTarget();// 1. 타겟 URL(동기), 2. 함수 명(비동기)

		Integer intPageTotal = (intTotalCount/ intCountPerPage);// 총 페이지 수
		Integer intPageGroupStart = (((intCurrentPage ) / intPageGroupSize) * intPageGroupSize);// 보여질 페이지 시작점 EX> 3페이지 부터
		Integer intPageGroupEnd = (intPageGroupStart + intPageGroupSize) ;// 보여질 페이지 끝점 EX> 5페이지 까지

		String[] arr = docIdArr.split(",");


		System.out.println("----------------------------------intTotalCount = " + intTotalCount);
		System.out.println("----------------------------------intPageTotal = " + intPageTotal);
		System.out.println("----------------------------------intPageGroupStart = " + intPageGroupStart);
		System.out.println("----------------------------------intPageGroupEnd = " + intPageGroupEnd);
		/* 예외조건 처리 - 1. 1페이지 이하 호출 , 2.총 페이지 이상 호출 시 처리 */
		if(intPageGroupStart < 0){intPageGroupStart = 0; };// 이전페이지의 동작으로 인해 음수의 페이지를 호출할 경우 1페이지로 이동
		if(intPageGroupEnd > intPageTotal){

			intPageGroupEnd = intPageTotal; 
		}// 보여질 페이지의 끝점이 토탈페이지 보다 클경우 마지막페이지로 이동
		System.out.println("----------------------------------intTotalCount = " + intTotalCount);
		System.out.println("----------------------------------intPageTotal = " + intPageTotal);
		System.out.println("----------------------------------intPageGroupStart = " + intPageGroupStart);
		System.out.println("----------------------------------intPageGroupEnd = " + intPageGroupEnd);

		/* 페이징 HTML 생성 START */
		StringBuffer strPagingBuf = new StringBuffer();

		strPagingBuf.append("<nav aria-label='Page navigation example'>\r\n");
        strPagingBuf.append("<ul class='pagination justify-content-center'>\r\n");

		// 1.처음 페이지
		strPagingBuf.append(makeButtonLinkByParams(strFirstDoc+"|0|FIRST", "BEGIN_TAG", strParams, strActionUrl, intCountPerPage, docIdArr));
/* 		
		// 2.이전 페이지
		if(intPageGroupStart >= intCountPerPage ){// 첫페이지가 아닐때
			LOG.info("이전1 - intPageGroupStart: " + intPageGroupStart + " / intCountPerPage: " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupStart-intPageGroupSize), "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else if(intPageGroupStart < intCountPerPage ){// 첫페이지 일때
			LOG.info("이전2 - intPageGroupStart: " + intPageGroupStart + " / intCountPerPage: " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams(0, "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}
*/		
		// 3.개별 페이징
		System.out.println("@@@@@@@@@@@@@@@@ @ @ @ @ @ @ @ @ @ @ @ @@ @ ");
		System.out.println(docIdArr);
		Integer docIndex = 0;
		for(int i = intPageGroupStart; i < intPageGroupEnd; i++){
			//Integer pageNum = i * intCountPerPage;
			
			Integer pageLinkNm = (i * intCountPerPage) / intCountPerPage;//(i*2) /5
			

			String strPageNum = String.valueOf(i);
			Integer intPageNum = Integer.valueOf(i);
			
			System.out.println("＠ @ @ @ @ @  @ @ @ =  arr[pageNum] : " + arr[docIndex] + "  //  strCurrensPage : " + strTargetDoc );
			
			//if(arr[pageNum].equals(strTargetDoc) || i == intCurrentPage){//선택된 페이지 일경우
			if(intPageNum == intCurrentPage){//선택된 페이지 일경우
				strPagingBuf.append("<strong class='page-link success' title='").append("" + String.valueOf((pageLinkNm+1)) + "").append("페이지(선택됨)'>").append(String.valueOf((pageLinkNm+1))).append("</strong>\r\n");
			}
			else{//비선택된 나머지 페이지의 경우
				strPagingBuf.append(makeLinkByParams(String.valueOf(arr[docIndex*intCountPerPage])+"|"+strPageNum+"|EACH", "" + String.valueOf((pageLinkNm+1)) + "",  strParams, strActionUrl, intCountPerPage, docIdArr));
			}
			docIndex++;
		}
 
		//4. 다음 페이지
		if((intPageGroupEnd * intCountPerPage) < intTotalCount){//호출 페이지가 마지막 페이지를 넘어가지 않을때(일반적인 다음페이지 호출)
			LOG.info("다음1 - intPageGroupEnd * intCountPerPage): " +  (intPageGroupEnd * intCountPerPage) + " / intTotalCount: " + intTotalCount);
			strPagingBuf.append(makeButtonLinkByParams(strNextDoc+"|"+String.valueOf(intPageGroupEnd)+"|NEXT"  , "NEXT_TAG", strParams, strActionUrl, intCountPerPage, docIdArr));
		}
		else{//호출 페이지가 마지막 페이지를 넘어갈때(마지막 페이지로 이동)
			LOG.info("다음2 - intPageGroupEnd * intCountPerPage): " +  (intPageGroupEnd * intCountPerPage) + " / intTotalCount: " + intTotalCount);
			strPagingBuf.append(makeButtonLinkByParams(strNextDoc+"|"+String.valueOf(intPageGroupEnd-1)+"|NEXT" , "NEXT_TAG", strParams, strActionUrl, intCountPerPage, docIdArr));
		}

		//5. 마지막 페이지
		strPagingBuf.append(makeButtonLinkByParams(strLastDoc+"|"+String.valueOf(intPageTotal)+"|LAST", "END_TAG", strParams, strActionUrl, intCountPerPage, docIdArr));

        strPagingBuf.append("</ul>\r\n");
		strPagingBuf.append("</nav>\r\n");

		/* 페이징 HTML 생성 END */
		
		LOG.info("--------------------Making Paging HTML--------------------");
		LOG.info(strPagingBuf.toString());
		LOG.info("----------------------------------------------------------");

        return strPagingBuf.toString();
    }

	/**
	 * @param intPageNum 클릭시 이동할 페이지 
	 * @param strLinkImg 특수 페지이 이미지 
	 * @param strParams 조건 파라메터 문자열로 구성된 배열
	 * @param actionUrl 리스트 호출 함수명 
	 * @param countPerPage 페이징 보여질 개시물 수
	 * @return 특수 페이징 HTML(처음, 이전, 다음, 마지막)
	 */
    private static String makeButtonLinkByParams(final String strDocId, final String strLinkImg, final String strParams, final String actionUrl, final int countPerPage, final String docIdArr){
		StringBuffer strLinkBuf = new StringBuffer();

		if("PREV_TAG".equals(strLinkImg)){
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='이전페이지' class='page-link text-success prev' href=\"javascript:");
		}
		else if("BEGIN_TAG".equals(strLinkImg)){
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='첫페이지' class='page-link text-success prev10' href=\"javascript:");
		}
		else if("END_TAG".equals(strLinkImg)){
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='마지막페이지' class='page-link text-success next10' href=\"javascript:");
		}
		else{
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='다음페이지' class='page-link text-success next' href=\"javascript:");
		}
		
		strLinkBuf.append(actionUrl).append("('").append(strDocId).append("',").append(countPerPage).append(",")
		.append("'"+strParams+"'").append(",").append("'"+docIdArr+"'").append(")\">");

		if("PREV_TAG".equals(strLinkImg)){//이전 페이지
			strLinkBuf.append("&lsaquo;</a></li>\r\n");
		}
		else if("BEGIN_TAG".equals(strLinkImg)){//첫 페이지
			strLinkBuf.append("&laquo;</a></li>\r\n");
		}
		else if("END_TAG".equals(strLinkImg)){//마지막 페이지
			strLinkBuf.append("&raquo;</a></li>\r\n");
		}
		else{//다음 페이지
			strLinkBuf.append("&rsaquo;</a>\r\n");
		}

		return strLinkBuf.toString();
	}

	/**
	 * @param intPageNum 클릭시 이동할 페이지 
	 * @param strLinkNum 개별 페이징 번호 
	 * @param strParams 조건 파라메터 문자열로 구성된 배열
	 * @param actionUrl 리스트 호출 함수명 
	 * @param countPerPage 페이징 보여질 개시물 수
	 * @return 개별페이징 HTML
	 */
	private static String makeLinkByParams(final String strDocId, final String strLinkNum, final String strParams, final String actionUrl, final int countPerPage, final String docIdArr){

		StringBuffer strLinkBuf = new StringBuffer();

		strLinkBuf
			.append("<li class='page-item'><a class='page-link text-success'")
			.append("title='").append(strLinkNum).append("페이지' ")
			.append("href=\"javascript:").append(actionUrl)
			.append("('").append(strDocId).append("',").append(countPerPage).append(",'").append(strParams).append("','")
			.append(docIdArr).append("')\">")
			.append(strLinkNum).append("</a></li>\r\n");

		return strLinkBuf.toString();
	}
}
