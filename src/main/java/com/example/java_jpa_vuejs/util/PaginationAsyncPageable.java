package com.example.java_jpa_vuejs.util;

import com.example.java_jpa_vuejs.common.PaginationDto;

public class PaginationAsyncPageable {
    public static String getDividePageFormByParams(PaginationDto paginationDto){

        Integer intCurrentPage = paginationDto.getCurrentPage();// 보여줄 현재 페이지
		Integer intCountPerPage = paginationDto.getCountPerPage();// 한페이지당 보여질 게시물 수
		Integer intPageGroupSize = paginationDto.getBlockPage();// 패아장을 제공할 페이지 블록 수
		Integer intTotalCount = paginationDto.getTotalCount();// 총 게시물 갯수

		String strParams = paginationDto.getParams();// 조건 파라메터
		String strActionUrl = paginationDto.getActionTarget();// 1. 타겟 URL(동기), 2. 함수 명(비동기)
		String strResType = paginationDto.getResType();// 추가적으로 조건이 필요할시 사용할 파라메터

		Integer intPageTotal = ((intTotalCount -1) / intCountPerPage);// 총 페이지 수
		Integer intPageGroupStart = (((intCurrentPage ) / intPageGroupSize) * intPageGroupSize);// 보여질 페이지 시작점 EX> 3페이지 부터
		Integer intPageGroupEnd = (intPageGroupStart + intPageGroupSize) ;// 보여질 페이지 끝점 EX> 5페이지 까지

		if(intPageGroupStart < 0){intPageGroupStart = 0; };// 이전페이지의 동작으로 인해 음수의 페이지를 호출할 경우 1페이지로 이동
		if(intPageGroupEnd > intPageTotal){intPageGroupEnd = intPageTotal +1; }// 보여질 페이지의 끝점이 토탈페이지 보다 클경우 마지막페이지로 이동

		StringBuffer strPagingBuf = new StringBuffer();

		strPagingBuf.append("<nav aria-label='Page navigation example'>\r\n");
        strPagingBuf.append("<ul class='pagination justify-content-center'>\r\n");

		//1.맨앞으로
		strPagingBuf.append(makeButtonLinkByParams(0, "BEGIN_TAG", strParams, strActionUrl, intCountPerPage));
		
		//2.이전 페이지
		if(intPageGroupStart >= intCountPerPage ){
			System.out.println("-----이전111  = " + intPageGroupStart + " / " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupStart-intPageGroupSize), "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else if(intPageGroupStart < intCountPerPage ){//첫페이지
			System.out.println("-----이전222  = "  + intPageGroupStart + " / " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams(0, "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}

		else if(intPageGroupStart < intCountPerPage ){
			System.out.println("-----이전333  = "  + intPageGroupStart + " / " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupStart-intPageGroupSize), "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}

		//3.개별 페이징
		for(int i = intPageGroupStart; i < intPageGroupEnd; i++){
			if( i == intCurrentPage){
				strPagingBuf.append("<strong class='page-link success' title='").append(i+1).append("페이지(선택됨)'>").append(i + 1).append("</strong>\r\n");
			}
			else{
				strPagingBuf.append(makeLinkByParams(i, "" + (i + 1) + "", strParams, strActionUrl, intCountPerPage));
			}
		}

		//4. 다음 페이지
		if((intPageGroupEnd * intCountPerPage) < intTotalCount){
			System.out.println("-----다음111   = " + ((intPageGroupEnd * intCountPerPage) < intTotalCount) + "     / " + (intPageGroupEnd * intCountPerPage));
			
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupEnd)  , "NEXT_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else{
			System.out.println("-----다음22   = " +  ((intPageGroupEnd * intCountPerPage) < intTotalCount) + "     / " + (intPageGroupEnd * intCountPerPage));
			
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupEnd -1 ) , "NEXT_TAG", strParams, strActionUrl, intCountPerPage));
		}

		//5. 마지막 페이지
		System.out.println("-----마지막22   = " +  ((intPageGroupEnd * intCountPerPage) < intTotalCount) + "     / " + (intPageGroupEnd * intCountPerPage) + "    intTotalCount / " + intTotalCount);
		strPagingBuf.append(makeButtonLinkByParams(intPageTotal, "END_TAG", strParams, strActionUrl, intCountPerPage));


        strPagingBuf.append("</ul>\r\n");
		strPagingBuf.append("</nav>\r\n");

		System.out.println("--------------------------------------------------------페이징 시작");
		System.out.println(strPagingBuf);
		System.out.println("--------------------------------------------------------페이징 끝");

        return strPagingBuf.toString();
    }

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

	private static String makeLinkByParams(final int intPage, final String strLinkNum, final String strParams, final String actionUrl, final int countPerPage){

		StringBuffer strLinkBuf = new StringBuffer();

		strLinkBuf.append(" <li class='page-item'><a class='page-link text-success'").append("title='").append(strLinkNum)
		.append("페이지' ").append("href=\"javascript:").append(actionUrl).append("(")
		.append(intPage).append(",").append(countPerPage).append(",").append(strParams)
		.append(")\">").append(strLinkNum).append("</a></li>\r\n");

		return strLinkBuf.toString();
	}
}
