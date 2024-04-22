package com.example.java_jpa_vuejs.common.repositoryServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.example.java_jpa_vuejs.common.configuration.FirebaseConfiguration;
import com.example.java_jpa_vuejs.common.model.PaginationDto;
import com.example.java_jpa_vuejs.common.repositoryService.PaginationFirebaseService;
import com.example.java_jpa_vuejs.common.utility.DateUtil;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import lombok.RequiredArgsConstructor;

@Service("paginationFirebaseService")
@RequiredArgsConstructor
public class PaginationFirbaseServiceImpl implements PaginationFirebaseService{

    final private static Logger LOG = Logger.getLogger(PaginationFirbaseServiceImpl.class.getName());

	private final FirebaseConfiguration firebaseConfiguration;
    
    
    /**
    * @method 컬렉션의 개별페이징을 위한 도큐먼트ID 세팅
    * @return 도큐먼트 ID리스트
    * @param  boardSq
    * @throws Exception
    */
    @Override
    public String getDocIdList(PaginationDto paginationDto) throws Exception {

        long reqTime = DateUtil.durationTime ("start", "CLOUD / < GET PAGING DOC LIST - SELECT > : ", 0, "Proceeding ::: " );

        LOG.info("도큐먼트ID 리스트 호출 - 기준 CurrentPage : " + paginationDto.getCurrentPage());

        String strDocIdList = "";//개별페이징을 위한 도큐먼트ID 배열

        String strCurrentDocId = paginationDto.getCurrentPage().split("\\|")[0];// 현재 도큐멘트 ID
        String strCurrentPage = paginationDto.getCurrentPage().split("\\|")[1]; // 현재 페이지 번호 
        String strCallType = paginationDto.getCurrentPage().split("\\|")[2];    // 현재 호출 타입
        String strCollectionNm = paginationDto.getCollectionNm();//도큐먼트를 가져올 컬렉션 명
        String strOrderbyCol = paginationDto.getOrderbyCol();//도큐먼트를 가져올 컬렉션의 정렬 컬럼

        Integer intCurrentPage = Integer.valueOf(strCurrentPage);
        Integer intCountPerPage = paginationDto.getCountPerPage();
        Integer intPageGroupSize = paginationDto.getBlockPage();
        Integer intTotalCount = paginationDto.getTotalCount();
        
        Integer callSize = (intCountPerPage * intPageGroupSize);// 개별페이징을 위해 호출해야 할 도큐먼트 사이즈
        Integer intPageTotal = (intTotalCount/ intCountPerPage);// 컬렉션의 총 페이지 수
        Integer intPageGroupStart = (((intCurrentPage ) / intPageGroupSize) * intPageGroupSize);// 보여질 페이지 시작점 EX> 3페이지 부터
        Integer intPageGroupEnd = (intPageGroupStart + intPageGroupSize) ;// 보여질 페이지 끝점 EX> 5페이지 까지

        List<String> docIdList = new ArrayList<String>();

        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            if(strCallType.equals("LAST")){

                Integer LastDocCnt = intTotalCount - (intPageGroupStart * intCountPerPage);//마지막 페이지그룹의 게시물 겟수
                
                // 컬렉션의 마지막에서 LastDocCnt만큼의 스냅샷 호출
                ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).limitToLast(LastDocCnt).get();
                
                List<QueryDocumentSnapshot> documents = query.get().getDocuments();

                // 도큐먼트 ID 배열 세팅
                for (QueryDocumentSnapshot document : documents) {
                    
                    String docId = document.getId();

                    docIdList.add(docId);
                }
                
                //배열을 중괄호를 제거한 문자열로 변환 하여 리턴
                strDocIdList = Arrays.toString(docIdList.toArray()).replaceAll("\\[","").replaceAll("\\]","");;
                
                return strDocIdList;
            }
            else if(strCallType.equals("NEXT")){
                
                /*                  
                 * 1. 마지막 페이지가 아닐때 
                 *      -> DocList를 업데이트 해야함 
                 * 
                 * 2. 마지막 페이지 일때.
                 *      -> 기존의 DocList를 사용
                 *   
                 * ※ 참고 ※ 
                 * 1. 몫 구하는 함수    : Math.floorDiv(26,10) = 2
                 * 2. 나머지 구하는 함수 : Math.floorMod(26,10) = 6   
                 * 3. 소수점 올림 : (int) Math.ceil(100 / (double) 3);
                 * 4. 소수점 내림 : (int) Math.floor(100 / (double) 3);
                 */
               
                // 1. 마지막 페이지그룹의 시작점과 끝점을 구해 포함될 경우 마지막페이지로 간주, 도큐먼트 리스트를 갱신하지 않는다.
                /*
                 * ★ ★★★★★★
                 */
                Integer intLastPagingStart = ((Math.floorDiv(intTotalCount, intCountPerPage) / intPageGroupSize) * intPageGroupSize)+1;
                Integer intLastPagingEnd1   = (int) Math.ceil(intTotalCount / intCountPerPage);
                Integer intLastPagingEnd   = (int) Math.ceil(intTotalCount / (double)intCountPerPage);
                
                LOG.info(String.valueOf(intLastPagingStart) + "      /      "+ intCurrentPage+ "     /     " + String.valueOf(intLastPagingEnd) + "   /   " + String.valueOf(intLastPagingEnd1));
                
                if(intLastPagingStart <= intCurrentPage-1 && intLastPagingEnd >= intCurrentPage-1){//마지막 페이지그룹일 때 도큐먼트 갱신 하지않음
                    LOG.info("다음 도큐먼트 리스트 호출 NEXT1");
                    strDocIdList = paginationDto.getDocIdArr();
                }
                else {
                    LOG.info("다음 도큐먼트 리스트 호출 NEXT2");
                    // 현재 페이지의 도큐먼트ID를 기준 으로 스냅샷 생성
                    ApiFuture<DocumentSnapshot> future = db.collection(strCollectionNm).document(strCurrentDocId).get();
                    DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);
                    
                    // 기준 도큐먼트ID 이후 필요 만큼 도큐먼트호출 
                    ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).startAt(snapshot).limit(callSize).get();

                    List<QueryDocumentSnapshot> documents = query.get().getDocuments();

                    // 도큐먼트ID 배열을 생성하여 중괄호를 제거한 문자열 형식으로 세팅
                    for (QueryDocumentSnapshot document : documents) {

                        String docId = document.getId();

                        docIdList.add(docId);
                    }

                    strDocIdList = Arrays.toString(docIdList.toArray()).replaceAll("\\[","").replaceAll("\\]","");;
                }

                return strDocIdList;
            }
            else if(strCallType.equals("PREV")){
                
                Integer intLastPagingStart = ((Math.floorDiv(intTotalCount, intCountPerPage) / intPageGroupSize) * intPageGroupSize)+1;;
                Integer intLastPagingEnd   = intPageTotal;
                

                if(intPageGroupStart < 0){//첫 페이지 일때
                    LOG.info("도큐멘트 리스트 호출 PREV1 - intPageGroupStart : " + intPageGroupStart);

                    strDocIdList = paginationDto.getDocIdArr();
                }
                else if(intLastPagingStart <= intCurrentPage && intLastPagingEnd >= intCurrentPage){//마지막 페이지 일때
                    LOG.info("도큐멘트 리스트 호출 PREV2 - intPageGroupStart : " + intPageGroupStart);

                    strDocIdList = paginationDto.getDocIdArr();
                }
                else{
                    LOG.info("도큐멘트 리스트 호출 PREV3 - intPageGroupStart : " + intPageGroupStart);

                    //햔제 도큐먼트ID를 기준으로 스냅샷 생성
                    ApiFuture<DocumentSnapshot> future = db.collection(strCollectionNm).document(strCurrentDocId).get();
                    DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);
                    
                    // 기준 도큐먼트ID 이후 필요 만큼 도큐먼트호출 
                    ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).startAt(snapshot).limit(callSize).get();
                    List<QueryDocumentSnapshot> documents = query.get().getDocuments();

                    // 도큐먼트ID 배열을 생성하여 중괄호를 제거한 문자열 형식으로 세팅
                    for (QueryDocumentSnapshot document : documents) {

                        String docId = document.getId();

                        docIdList.add(docId);
                    }

                    strDocIdList = Arrays.toString(docIdList.toArray()).replaceAll("\\[","").replaceAll("\\]","");;
                }

                return strDocIdList;
            }
            else if(strCallType.equals("FIRST")){
                ApiFuture<DocumentSnapshot> future = db.collection(strCollectionNm).document(strCurrentDocId).get();
                DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);
                
                // 기준 도큐먼트ID 이후 필요 만큼 도큐먼트호출 
                ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).startAt(snapshot).limit(callSize).get();
                List<QueryDocumentSnapshot> documents = query.get().getDocuments();

                // 도큐먼트ID 배열을 생성하여 중괄호를 제거한 문자열 형식으로 세팅
                for (QueryDocumentSnapshot document : documents) {

                    String docId = document.getId();

                    docIdList.add(docId);
                }

                strDocIdList = Arrays.toString(docIdList.toArray()).replaceAll("\\[","").replaceAll("\\]","");; 
                
                return strDocIdList;
            }
            else if(!strCallType.equals("EACH")){
                
                if((intPageGroupEnd * intCountPerPage) >= intTotalCount){//마지막 페이지 일때
                    LOG.info("도큐멘트 리스트 호출 EACH1 - intPageGroupStart : " + intPageGroupStart);
                    strDocIdList = paginationDto.getDocIdArr();
                }
                else{
                    LOG.info("도큐멘트 리스트 호출 EACH2 - intPageGroupStart : " + intPageGroupStart);
                    //햔제 도큐먼트ID를 기준으로 스냅샷 생성
                    ApiFuture<DocumentSnapshot> future = db.collection(strCollectionNm).document(strCurrentDocId).get();
                    DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);
                    
                    // 기준 도큐먼트ID 이후 필요 만큼 도큐먼트호출 
                    ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).startAt(snapshot).limit(callSize).get();
                    List<QueryDocumentSnapshot> documents = query.get().getDocuments();

                    // 도큐먼트ID 배열을 생성하여 중괄호를 제거한 문자열 형식으로 세팅
                    for (QueryDocumentSnapshot document : documents) {

                        String docId = document.getId();

                        docIdList.add(docId);
                    }

                    strDocIdList = Arrays.toString(docIdList.toArray()).replaceAll("\\[","").replaceAll("\\]","");
                }

                return strDocIdList;
            }
            else{
                strDocIdList = paginationDto.getDocIdArr();

                return strDocIdList;
            }
        }
        catch (Exception e) {
			DateUtil.durationTime ("end", "CLOUD / < GET PAGING DOC LIST - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return strDocIdList;
    }


    /**
    * @method 컬렉션의 첫번째 도큐먼트ID 세팅
    * @return 첫번째 도큐멘트 ID
    * @param  paginationDto
    * @throws Exception
    */
    @Override
    public String getFirstDoc(PaginationDto paginationDto) throws Exception {

        long reqTime = DateUtil.durationTime ("start", "CLOUD / < GET PAGING FIRST DOC - SELECT > : ", 0, "Proceeding ::: " );

        String strFirstDoc = "";//첫번째 도큐먼트ID
        String intCurrentPage = paginationDto.getCurrentPage();//현재 페이지 정보
        String strCollectionNm = paginationDto.getCollectionNm();//도큐먼트를 가져올 컬렉션 명
        String strOrderbyCol = paginationDto.getOrderbyCol();//도큐먼트를 가져올 컬렉션의 정렬 컬럼

        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            // 쿼리 스냅샷 호출 후 첫번째 도큐먼트ID 세팅
            ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).limit(1).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                
                strFirstDoc = document.getId() + "|1|FIRST";
            }

            // 현재 페이지 정보가 없을 경우 위에서 호출 한 첫번째 도큐멘트ID로 현재페이지를 세팅
            if(intCurrentPage.equals("1")){
                paginationDto.setCurrentPage(strFirstDoc);
            }
            
            DateUtil.durationTime ("end", "CLOUD / < GET PAGING FIRST DOC - SELECT > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			DateUtil.durationTime ("end", "CLOUD / < GET PAGING FIRST DOC - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return strFirstDoc;
    }


    /**
    * @method 컬렉션의 이전 도큐멘트ID 세팅
    * @return 마지막 도큐멘트 ID
    * @param  paginationDto 
    * @throws Exception
    */
    @Override
    public String getPrevDoc(PaginationDto paginationDto) throws Exception {
        
        String strPrevDoc;
        String strCollectionNm = paginationDto.getCollectionNm();//도큐먼트를 가져올 컬렉션 명
        String strOrderbyCol = paginationDto.getOrderbyCol();//도큐먼트를 가져올 컬렉션의 정렬 컬럼
        String strCallType = paginationDto.getCurrentPage().split("\\|")[2].trim();//페이징 도큐먼트 호출 타입

        Integer intCurrentPage = Integer.valueOf(paginationDto.getCurrentPage().split("\\|")[1].trim());//현재 문서 페이지
        Integer intCountPerPage = paginationDto.getCountPerPage();// 페이지에서 제공할 게시물의 수
		Integer intPageGroupSize = paginationDto.getBlockPage();// 패아장을 제공할 페이지 그룹의 수
        Integer intTotalCount = paginationDto.getTotalCount();// 총 게시물 갯수
    
        Integer intPageGroupStart = (((intCurrentPage ) / intPageGroupSize) * intPageGroupSize);//다음 페이지 그룹의 시작점
        Integer intPageGroupEnd = (intPageGroupStart + intPageGroupSize) ;// 보여질 페이지 끝점 EX> 5페이지 까지

        /*
         * 1. if      - 첫페이지일 때: 첫번째 도큐먼트iD로 세팅
         * 2. else if - 마지막 페이지 일때 : 한블럭의 게시물수 + 마지막 블럭의 게시물수를 합쳐 이전 도큐먼트ID 세팅  
         * 3. else    - 첫페이지가 아닐 때: 현재 도큐멘트 배열의 첫번째 값으로 세팅
         */
        if(intPageGroupStart <= intPageGroupSize){//첫페이지일때 - 첫번째 도큐먼트ID를 이전버튼 도큐멘트ID로 세팅
    
            strPrevDoc = paginationDto.getFirstDoc().split("\\|")[0];
            
            LOG.info("이전 게시물 아이디 세팅 Type1 : PrevDoc -> " + strPrevDoc);
        }
        else if((intPageGroupEnd * intCountPerPage) >= intTotalCount || // 1. 가는 곳이 마지막페이지 일 때
                (intPageGroupEnd * intCountPerPage) >= (intTotalCount - (intPageGroupSize * intCountPerPage))){// 2. 마지막페이지에서 이전버튼 눌렸을 때
            
            /*
            * 1. 마지막 페이지의 경우 : 마지막 페이지그룹의 게시물 갯수 와 한블록당 게시물 갯수의 합을 구하여 이전 도큐먼트ID를 호출 세팅
            * 2. 마지막 페이지에서 이전버튼 눌렸을 경우 : 마지막 페이지그룹의 게시물 갯수 와 한블록당 게시물 갯수의 합을 구하여 이전 도큐먼트ID를 호출 세팅
            */
            
            Integer LastDocCnt = intTotalCount - (intPageGroupStart * intCountPerPage);//마지막 페이지그룹의 게시물 갯수
            Integer prevDocCnt = LastDocCnt + (intCountPerPage * intPageGroupSize); //마지막 페이지 개시물 갯수 + 한블럭당 보여질 게시물 갯수
            
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).limitToLast(prevDocCnt).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            QueryDocumentSnapshot lastDoc = documents.get(0);

            strPrevDoc = lastDoc.getId();

            LOG.info("이전 게시물 아이디 세팅 Type2 : PrevDoc -> " + strPrevDoc);
        }
        else if(strCallType.equals("EACH")){//3. 개별페이징일 경우 - 현재 DocList의 첫번째를 기준으로 이전 페이지의 이전버튼 도큐멘트ID 세팅
                
            strPrevDoc = paginationDto.getDocIdArr().split(",")[0];

            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            Integer callSize = intPageGroupSize*intCountPerPage;

            ApiFuture<DocumentSnapshot> future = db.collection(strCollectionNm).document(strPrevDoc).get();
            DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);

            ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).endBefore(snapshot).limitToLast(callSize).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            QueryDocumentSnapshot lastDoc = documents.get(0);

            strPrevDoc = lastDoc.getId();

            LOG.info("이전 게시물 아이디 세팅 Type3 : PrevDoc -> " + strPrevDoc);
        }
        else{//4. 기본 이전페이지 - 갱신전 DocList의 첫 도큐먼트ID 이전페이지로 설정
            
            strPrevDoc = paginationDto.getDocIdArr().split(",")[0];

            LOG.info("이전 게시물 아이디 세팅 Type4 : PrevDoc -> " + strPrevDoc);
        }

        return strPrevDoc;
    }


    /*
    * @method 컬렉션의 다음 도큐멘트ID 세팅
    * @return 다음 도큐멘트 ID
    * @param  paginationDto
    * @throws Exception
    */
    @Override
    public String getNextDoc(PaginationDto paginationDto) throws Exception {

        long reqTime = DateUtil.durationTime ("start", "CLOUD / < GET PAGING NEXT DOC - SELECT > : ", 0, "Proceeding ::: " );
        
        String strNextDocId = "";
        
        String[] docIdArr = paginationDto.getDocIdArr().split(",");//현재 도큐먼트ID 배열
        String strDocIdArrLast = docIdArr[docIdArr.length-1].trim();//배열의 자미가 도큐먼트ID를 다음페이지의 기준으로 사용
        String strCollectionNm = paginationDto.getCollectionNm();//도큐먼트를 가져올 컬렉션 명
        String strOrderbyCol = paginationDto.getOrderbyCol();//도큐먼트를 가져올 컬렉션의 정렬 컬럼

        LOG.info("다음 게시물 아이디 세팅 : NextDoc -> " + strDocIdArrLast);

        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //헌재 페이지의 기준 도큐먼트ID로 호출하여 스넵샷 세팅 
            ApiFuture<DocumentSnapshot> future = db.collection(strCollectionNm).document(strDocIdArrLast).get();
            DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);
                
            //위의 스냅샷 이후를 기준으로 호출 하여 다음 페이지의 기준 도큐먼트ID 호출
            ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).startAfter(snapshot).limit(1).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                
                strNextDocId = document.getId();
            }

            DateUtil.durationTime ("end", "CLOUD / < GET PAGING NEXT DOC - SELECT > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			DateUtil.durationTime ("end", "CLOUD / < GET PAGING NEXT DOC - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return strNextDocId;
    }


    /**
    * @method 컬렉션의 마지막 도큐먼트ID 세팅
    * @return 마지막 도큐멘트 ID
    * @param  boardSq
    * @throws Exception
    */
    @Override
    public String getLastDoc(PaginationDto paginationDto) throws Exception {

        long reqTime = DateUtil.durationTime ("start", "CLOUD / < GET PAGING LAST DOC - SELECT > : ", 0, "Proceeding ::: " );

        String strLastDocId = "";//마지막 도큐먼트 ID
        String strCollectionNm = paginationDto.getCollectionNm();//도큐먼트를 가져올 컬렉션 명
        String strOrderbyCol = paginationDto.getOrderbyCol();//도큐먼트를 가져올 컬렉션의 정렬 컬럼
        
        Integer countPerPage = paginationDto.getCountPerPage();//페이지당 보여줄 게시물 수

        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            // 컬렉션의 마지막으로 부터 CountPerPage만큼의 게시물을 호출
            ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).limitToLast(countPerPage).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            // 해당 리스트의 첫번째 게시물을 마지막페이지의 기준 도큐먼트ID로 세팅
            QueryDocumentSnapshot lastDoc = documents.get(0);

            strLastDocId = lastDoc.getId();

            DateUtil.durationTime ("end", "CLOUD / < GET PAGING LAST DOC - SELECT > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			DateUtil.durationTime ("end", "CLOUD / < GET PAGING LAST DOC - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return strLastDocId;
    }



    /**
    * @method 컬렉션 총 게시물 수 가져오기
    * @param  paginationDto
    * @throws Exception
    */
    @Override
    public Integer getTotalCount(PaginationDto paginationDto) throws Exception {
        
        long reqTime = DateUtil.durationTime ("start", "CLOUD / < GET BOARD TOTALCOUNT - SELECT > : ", 0, "Proceeding ::: " );
        
        String strCollectionNm = paginationDto.getCollectionNm();//도큐먼트를 가져올 컬렉션 명
        String strOrderbyCol = paginationDto.getOrderbyCol();//도큐먼트를 가져올 컬렉션의 정렬 컬럼

        Integer totalCount = 0;
        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            AggregateQuerySnapshot snapshot = db.collection(strCollectionNm).orderBy(strOrderbyCol).count().get().get();
            LOG.info("Get ToalCount - " + snapshot.getCount());

            DateUtil.durationTime ("end", "CLOUD / < GET BOARD TOTALCOUNT - SELECT > : ", reqTime, "Complete ::: " );

           totalCount =  (int) snapshot.getCount();
        }
        catch (Exception e) {
			DateUtil.durationTime ("end", "CLOUD / < GET BOARD TOTALCOUNT - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return totalCount;
    }
}
    