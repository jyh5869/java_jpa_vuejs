package com.example.java_jpa_vuejs.util;

import com.example.java_jpa_vuejs.common.PaginationDto;

public class PaginationAsyncPageable {
    public static String getDividePageFormByParams(PaginationDto paginationDto){

        Integer intCurrentPage = paginationDto.getCurrentPage();
		Integer intCountPerPage = paginationDto.getCountPerPage();// 한페이지당 몇개씩 보여줄건지
		Integer intPageGroupSize = paginationDto.getBlockPage();// 패아장 몇페이지까지 보여줄건지

		Integer intTotalCount = paginationDto.getTotalCount();
		String strResType = paginationDto.getResType();
		String strParams = paginationDto.getParams();
		String strActionUrl = paginationDto.getActionTarget();

		Integer intPageTotal = (intTotalCount / intCountPerPage);
		Integer intPageGroupStart = (((intCurrentPage) / intPageGroupSize) * intPageGroupSize);
		Integer intPageGroupEnd = (intPageGroupStart + intPageGroupSize);

		System.out.println("@@@@@@      intTotalCount     = " + intTotalCount);
		System.out.println("@@@@@@      intPageTotal      = " + intPageTotal);
		System.out.println("@@@@@@      intPageGroupStart = " + intPageGroupStart);
		System.out.println("@@@@@@      intPageGroupEnd   = " + intPageGroupEnd);

		if(intPageGroupStart <= 0){intPageGroupStart = 0; }; 
		if(intPageGroupEnd > intPageTotal){intPageGroupEnd = intPageTotal+1; }; 

		System.out.println("######      intTotalCount     = " + intTotalCount);
		System.out.println("######      intPageTotal      = " + intPageTotal);
		System.out.println("######      intPageGroupStart = " + intPageGroupStart);
		System.out.println("######      intPageGroupEnd   = " + intPageGroupEnd);

		StringBuffer strPagingBuf = new StringBuffer();

		strPagingBuf.append("<nav aria-label='Page navigation example'>\r\n");
        strPagingBuf.append("<ul class='pagination justify-content-center'>\r\n");

		//1.맨앞으로
		strPagingBuf.append(makeButtonLinkByParams(0, "BEGIN_TAG", strParams, strActionUrl, intCountPerPage));
		
		//2.이전 페이지
		if(intPageGroupStart > intCountPerPage ){
			System.out.println("-----이전111  = " + intPageGroupStart + " / " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupStart -1)-intCountPerPage, "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else if(intPageGroupStart <= intCountPerPage ){//첫페이지
			System.out.println("-----이전222  = "  + intPageGroupStart + " / " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams(0, "PREV_TAG", strParams, strActionUrl, intCountPerPage));
		}
		/* */
		else if(intPageGroupStart < intCountPerPage ){
			System.out.println("-----이전333  = "  + intPageGroupStart + " / " + intCountPerPage);
			strPagingBuf.append(makeButtonLinkByParams((intPageGroupStart -1)-intCountPerPage, "PREV_TAG", strParams, strActionUrl, intCountPerPage));
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
			
			strPagingBuf.append(makeButtonLinkByParams(intPageGroupEnd, "NEXT_TAG", strParams, strActionUrl, intCountPerPage));
		}
		else{
			System.out.println("-----다음22   = " +  ((intPageGroupEnd * intCountPerPage) < intTotalCount) + "     / " + (intPageGroupEnd * intCountPerPage));
			
			strPagingBuf.append(makeButtonLinkByParams(intPageGroupEnd-1, "NEXT_TAG", strParams, strActionUrl, intCountPerPage));
		}

		//5. 마지막 페이지
		//if("total".equalsIgnoreCase(strResType)){
		//	if((intPageGroupEnd * intCountPerPage) < intTotalCount){
		//		System.out.println("-----마지막11   = " +  ((intPageGroupEnd * intCountPerPage) < intTotalCount) + "     / " + (intPageGroupEnd * intCountPerPage) + "    intTotalCount / " +intTotalCount);
		//		strPagingBuf.append(makeButtonLinkByParams(intPageTotal-1, "END_TAG", strParams, strActionUrl, intCountPerPage));
		//	}
		//	else{
				System.out.println("-----마지막22   = " +  ((intPageGroupEnd * intCountPerPage) < intTotalCount) + "     / " + (intPageGroupEnd * intCountPerPage) + "    intTotalCount / " + intTotalCount);
				strPagingBuf.append(makeButtonLinkByParams(intPageTotal, "END_TAG", strParams, strActionUrl, intCountPerPage));
		//	}
		//}

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
