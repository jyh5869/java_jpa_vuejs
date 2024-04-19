package com.example.java_jpa_vuejs.common.utility;

import java.util.logging.Logger;

import com.example.java_jpa_vuejs.common.model.PaginationDto;

public class PaginationAsyncPageable {

	final private static Logger LOG = Logger.getLogger(PaginationAsyncPageable.class.getName());

    public static String getDividePageFormByParams(PaginationDto paginationDto){

        Integer intCurrentPage = Integer.valueOf(paginationDto.getCurrentPage());// 보여줄 현재 페이지
		Integer intCountPerPage = Integer.valueOf(paginationDto.getCountPerPage());// 한페이지당 보여질 게시물 수
		Integer intPageGroupSize = Integer.valueOf(paginationDto.getBlockPage());// 패아장을 제공할 페이지 블록 수
		Integer intTotalCount = Integer.valueOf(paginationDto.getTotalCount());// 총 게시물 갯수

		String strParams = paginationDto.getParams();// 조건 파라메터
		String strActionUrl = paginationDto.getActionTarget();// 1. 타겟 URL(동기), 2. 함수 명(비동기)
		String strCallType = paginationDto.getCallType();// 추가적으로 조건이 필요할시 사용할 파라메터

		Integer intPageTotal = ((intTotalCount -1) / intCountPerPage);// 총 페이지 수
		Integer intPageGroupStart = (((intCurrentPage ) / intPageGroupSize) * intPageGroupSize);// 보여질 페이지 시작점 EX> 3페이지 부터
		Integer intPageGroupEnd = (intPageGroupStart + intPageGroupSize) ;// 보여질 페이지 끝점 EX> 5페이지 까지

		/* 예외조건 처리 - 1. 1페이지 이하 호출 , 2.총 페이지 이상 호출 시 처리 */
		if(intPageGroupStart < 0){intPageGroupStart = 0; };// 이전페이지의 동작으로 인해 음수의 페이지를 호출할 경우 1페이지로 이동
		if(intPageGroupEnd > intPageTotal){intPageGroupEnd = intPageTotal +1; }// 보여질 페이지의 끝점이 토탈페이지 보다 클경우 마지막페이지로 이동


		/* 페이징 HTML 생성 START */
		StringBuffer strPagingBuf = new StringBuffer();

		strPagingBuf.append("<nav aria-label='Page navigation example'>\r\n");
        strPagingBuf.append("<ul class='pagination justify-content-center'>\r\n");

		// 1.처음 페이지
		strPagingBuf.append(makeButtonLinkByParams(0, "BEGIN_TAG", strParams, strActionUrl, intCountPerPage));
		
		// 2.이전 페이지
		if(intPageGroupStart >= intCountPerPage ){// 첫페이지가 아닐때
			LOG.info("이전1 - intPageGroupStart: " + intPageGroupStart + " / intCountPerPage: " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupStart-intPageGroupSize), "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else if(intPageGroupStart < intCountPerPage ){// 첫페이지 일때
			LOG.info("이전2 - intPageGroupStart: " + intPageGroupStart + " / intCountPerPage: " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams(0, "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}

		// 3.개별 페이징
		for(int i = intPageGroupStart; i < intPageGroupEnd; i++){
			if( i == intCurrentPage){// 선택된 페이지 일때
				strPagingBuf.append("<strong class='page-link success' title='").append(i+1).append("페이지(선택됨)'>").append(i + 1).append("</strong>\r\n");
			}
			else{//선택된 페이지가 아닐때
				strPagingBuf.append(makeLinkByParams(i, "" + (i + 1) + "",  strParams, strActionUrl, intCountPerPage));
			}
		}

		//4. 다음 페이지
		if((intPageGroupEnd * intCountPerPage) < intTotalCount){//호출 페이지가 마지막 페이지를 넘어가지 않을때(일반적인 다음페이지 호출)
			LOG.info("다음1 - intPageGroupEnd * intCountPerPage): " +  (intPageGroupEnd * intCountPerPage) + " / intTotalCount: " + intTotalCount);
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupEnd)  , "NEXT_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else{//호출 페이지가 마지막 페이지를 넘어갈때(마지막 페이지로 이동)
			LOG.info("다음2 - intPageGroupEnd * intCountPerPage): " +  (intPageGroupEnd * intCountPerPage) + " / intTotalCount: " + intTotalCount);
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupEnd -1 ) , "NEXT_TAG", strParams, strActionUrl, intCountPerPage));
		}

		//5. 마지막 페이지
		strPagingBuf.append(makeButtonLinkByParams(intPageTotal, "END_TAG", strParams, strActionUrl, intCountPerPage));

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
    private static String makeButtonLinkByParams(final int intPageNum, final String strLinkImg, final String strParams, final String actionUrl, final int countPerPage){
		StringBuffer strLinkBuf = new StringBuffer();

		if("PREV_TAG".equals(strLinkImg)){
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='이전페이지' class='page-link text-success prev' href='javascript:");
		}
		else if("BEGIN_TAG".equals(strLinkImg)){
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='첫페이지' class='page-link text-success prev10' href='javascript:");
		}
		else if("END_TAG".equals(strLinkImg)){
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='마지막페이지' class='page-link text-success next10' href='javascript:");
		}
		else{
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='다음페이지' class='page-link text-success next' href='javascript:");
		}

		strLinkBuf.append(actionUrl).append("(").append(intPageNum).append(",").append(countPerPage).append(",").append(strParams).append(")'>");

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
	private static String makeLinkByParams(final int intPageNum, final String strLinkNum, final String strParams, final String actionUrl, final int countPerPage){

		StringBuffer strLinkBuf = new StringBuffer();

		strLinkBuf
			.append(" <li class='page-item'><a class='page-link text-success'")
			.append("title='").append(strLinkNum).append("페이지' ")
			.append("href=\"javascript:").append(actionUrl)
			.append("(").append(intPageNum).append(",").append(countPerPage).append(",").append(strParams).append(")\">")
			.append(strLinkNum).append("</a></li>\r\n");

		return strLinkBuf.toString();
	}
}
