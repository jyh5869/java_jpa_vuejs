package com.example.java_jpa_vuejs.common.utility;

import com.example.java_jpa_vuejs.common.model.PaginationDto;

public class PaginationSync {
    public static String getDividePageFormByParams(PaginationDto paginationDto){

        Integer intCurrentPage = Integer.valueOf(paginationDto.getCurrentPage());
		Integer intCountPerPage = 1;
		Integer intPageGroupSize = paginationDto.getCountPerPage();
		Integer intTotalCount = paginationDto.getTotalCount();
		
		String callType = paginationDto.getCallType();
		String strResType = paginationDto.getCallType();
		String strParams = "";
		String strActionUrl = "";

		Integer intPageTotal = ((intTotalCount - intCountPerPage) / intCountPerPage);
		Integer intPageGroupStart = (((intCurrentPage -1) / intPageGroupSize) * intPageGroupSize);
		Integer intPageGroupEnd = intPageGroupStart + intPageGroupSize;

		if(intPageGroupEnd > intTotalCount){intPageGroupEnd = intPageTotal + 1; }; 

		StringBuffer strPagingBuf = new StringBuffer();

		strPagingBuf.append("<nav aria-label='Page navigation example'>\r\n");
        strPagingBuf.append("<ul class='pagination justify-content-center'>\r\n");

		//1.맨앞으로
		strPagingBuf.append(makeButtonLinkByParams(1, "BEGIN_TAG", strParams, strActionUrl, intCountPerPage));
		
		//2.이전 페이지
		if(intPageGroupStart >= intCountPerPage ){
			strPagingBuf.append(makeButtonLinkByParams(intPageGroupStart, "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else if(intPageGroupStart <= intCountPerPage ){
			strPagingBuf.append(makeButtonLinkByParams(1, "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else if(intPageGroupStart < intCountPerPage ){
			strPagingBuf.append(makeButtonLinkByParams(intPageGroupStart, "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}

		//3.개별 페이징
		for(int i = intPageGroupStart; i < intPageGroupEnd; i++){
			if( (i+1) == intCurrentPage){
				strPagingBuf.append("<strong title='").append(i+1).append("페이지(선택됨)'>").append(i + 1).append("</strong>\r\n");
			}
			else{
				strPagingBuf.append(makeLinkByParams(i + 1, "" + (i + 1) + "", strParams, strActionUrl, intCountPerPage));
			}
		}

		//4. 다음 페이지
		if((intPageGroupEnd * intCountPerPage) < intTotalCount){
			strPagingBuf.append(makeButtonLinkByParams(intPageTotal+1, "NEXT_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else{
			strPagingBuf.append(makeButtonLinkByParams(intPageTotal, "NEXT_TAG", strParams, strActionUrl, intCountPerPage));
		}

		//5. 마지막 페이지
		if("total".equalsIgnoreCase(strResType)){
			strPagingBuf.append(makeButtonLinkByParams(intPageTotal+1, "END_TAG", strParams, strActionUrl, intCountPerPage));
		}

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
			strLinkBuf.append("<a title='이전페이지' class='page-link text-success prev' href='");
		}
		else if("BEGIN_TAG".equals(strLinkImg)){
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='첫페이지' class='page-link text-success prev10' href='");
		}
		else if("END_TAG".equals(strLinkImg)){
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='마지막페이지' class='page-link text-success next10' href='");
		}
		else{
            strLinkBuf.append("<li class='page-item'>");
			strLinkBuf.append("<a title='다음페이지' class='page-link text-success next' href='");
		}

		strLinkBuf.append(actionUrl).append("?currentPage=").append(intPageNum==0?1:intPageNum).append("&countPerPage=").append(countPerPage).append(strParams).append("'>");

		if("PREV_TAG".equals(strLinkImg)){//이전 페이지
			strLinkBuf.append("&lsaquo;</a></li>\r\n");
		}
		else if("BEGIN_TAG".equals(strLinkImg)){//첫 페이지
			strLinkBuf.append("&laquo;</a></li>\r\n");
		}
		else if("END_TAG".equals(strLinkImg)){//마지막 페이지
			strLinkBuf.append("&rsaquo;</a></li>\r\n");
		}
		else{//다음 페이지
			strLinkBuf.append("&raquo;</a>\r\n");
		}

		return strLinkBuf.toString();
	}

	private static String makeLinkByParams(final int intPage, final String strLinkNum, final String strParams, final String actionUrl, final int countPerPage){

		StringBuffer strLinkBuf = new StringBuffer();

		strLinkBuf.append(" <li class='page-item'><a class='page-link text-success'").append("title='").append(strLinkNum).append("페이지' ").append("href=\"").append(actionUrl).append("?currentPage=")
		.append(intPage).append("&countPerPage=").append(countPerPage).append("&").append(strParams).append("\">").append(strLinkNum).append("</a></li>\r\n");

		return strLinkBuf.toString();
	}
}
