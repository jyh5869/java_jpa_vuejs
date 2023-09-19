package com.example.java_jpa_vuejs.geomBoard.repositoryServiceImpl;

import com.common.Util;
import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;
import com.example.java_jpa_vuejs.geomBoard.BoardDto;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardFirebaseService;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.cloud.FirestoreClient;

import io.opencensus.metrics.export.Summary.Snapshot;

@Service("boardFirebaseService")
@RequiredArgsConstructor
public class BoardFirebaseServiceImpl implements BoardFirebaseService {

    final private static Logger LOG = Logger.getLogger(BoardFirebaseServiceImpl.class.getName());

	private final FirebaseConfiguration firebaseConfiguration;

	private final ModelMapper modelMapper;

    /**
    * @method 지오메트릭 데이터 등록하기
    * @param id
    * @param boardDTO
    * @throws Exception
    */
	@Override
	public void setGeomdData(long id, String geomPolygons) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / < SET GEOMETRY DATA - INSERT> : ", 0, "Proceeding ::: " );

        try {
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();
            
            String lastIdx = String.valueOf(id);
            String geomData = URLDecoder.decode(geomPolygons.toString(), "UTF-8");

            JSONObject jObject = new JSONObject(geomData);

            String features = jObject.getString("features");
            
            JSONArray geomArray = new JSONArray(features);

            for(int i = 0 ; i < geomArray.length(); i ++){
                JSONObject obj = geomArray.getJSONObject(i);
                //System.out.println(obj.toString());

                String dataType = obj.getString("type");
                //System.out.println("dataType(" + i + "): " + dataType);

                String geometry = obj.getString("geometry");
                //System.out.println("geometry(" + i + "): " + geometry);
                String properties = obj.getString("properties")
                ;
                JSONObject propObj = new JSONObject(properties);
                JSONObject geoObj = new JSONObject(geometry);
                String geomType = geoObj.getString("type");
                String state = propObj.getString("state");

                SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String regDt = dayTime.format(new Date());
                Map<String, Object> docData = new HashMap<String, Object>();

                docData.put("id", id);
                docData.put("data_type", dataType);
                docData.put("geom_type", geomType);
                docData.put("geom_value", geometry);
                docData.put("geom_properties", properties);
                docData.put("reg_dt", regDt);

                if(state.equals("insert")){//인서트
                    ApiFuture<WriteResult> future = db.collection("geometry").document().set(docData);
                }
                else if(state.equals("update")){//업데이트
                    String docId = obj.getString("id");
                    ApiFuture<WriteResult> future = db.collection("geometry").document(docId).set(docData);
                }
            }    
                
            Util.durationTime ("end", "CLOUD / < SET GEOMETRY DATA - INSERT > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "CLOUD / < SET GEOMETRY DATA - INSERT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
	}

    /**
    * @method 지오메트릭 게시글 등록하기
    * @param id
    * @param boardDTO
    * @throws Exception
    */
    @Override
	public void setBoardData(long id, BoardDto boardDTO) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / < SET GEOMETRY DATA - UPDATE > : ", 0, "Proceeding ::: " );

        try {
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();
            
            String updateId = String.valueOf(boardDTO.getDocId());

            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String regDt = dayTime.format(new Date());
            Map<String, Object> docData = new HashMap<String, Object>();

            docData.put("board_sq", boardDTO.getBoardSq());
            docData.put("user_email", boardDTO.getUserEmail());
            docData.put("user_name", boardDTO.getUserNm());
            docData.put("user_adress", boardDTO.getUserAdress());
            docData.put("title", boardDTO.getTitle() );
            docData.put("contents1", boardDTO.getContents1());
            docData.put("contents2", boardDTO.getContents2());
            docData.put("state", boardDTO.getState());
            docData.put("use_yn", boardDTO.getUseYn());
            docData.put("zip_cd", boardDTO.getZipCd());
            docData.put("reg_dt", regDt);
            
            String state = boardDTO.getActionType();

            if(state.equals("insert")){//인서트
                ApiFuture<WriteResult> future = db.collection("geometry_board").document().set(docData);
            }
            else if(state.equals("update")){//업데이트

                ApiFuture<WriteResult> future = db.collection("geometry_board").document(updateId).set(docData);
            }
            
            Util.durationTime ("end", "CLOUD / < SET GEOMETRY DATA - UPDATE > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "CLOUD / < SET GEOMETRY DATA - UPDATE > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
	}

    /**
    * @method 지오메트릭 게시글 리스트 가져오기
    * @param  paginationDto
    * @throws Exception
    */
    @Override
	public List<Map<String, Object>> getGeomBoardList(PaginationDto paginationDto) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / < GET GEOMETRY BOARD LIST - SELECT > : ", 0, "Proceeding ::: " );
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

		Integer currentPage = Integer.valueOf(paginationDto.getCurrentPage());
        Integer countPerPage = Integer.valueOf(paginationDto.getCountPerPage());

        String startAt = String.valueOf((currentPage * countPerPage)-1) ;

		try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry_board").orderBy("board_sq", Direction.ASCENDING).whereGreaterThan("board_sq",  startAt).limit(countPerPage).get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {

                Map<String, Object> data = document.getData();
                data.put("docId", document.getId());
                data.put("createdDate", data.get("reg_dt"));
                data.put("userAdress", data.get("user_adress"));
                data.put("userEmail", data.get("user_email"));
                data.put("userNm", data.get("user_name"));
            
                result.add(data);
            }
            Util.durationTime ("end", "CLOUD / < GET GEOMETRY BOARD LIST - SELECT > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < GET GEOMETRY BOARD LIST - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
		
		return result;
	}
    
    /**
    * @method 지오메트릭 게시글 리스트 가져오기
    * @param  paginationDto
    * @throws Exception
    */
    @Override
	public List<Map<String, Object>> getGeomBoardList2(PaginationDto paginationDto) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / < GET GEOMETRY BOARD LIST - SELECT > : ", 0, "Proceeding ::: " );
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

		String currentDocId = String.valueOf(paginationDto.getCurrentPage().split("\\|")[0]);
        String currentPage = String.valueOf(paginationDto.getCurrentPage().split("\\|")[1]);
        Integer countPerPage = Integer.valueOf(paginationDto.getCountPerPage());

        //String startAt = String.valueOf((currentPage * countPerPage)-1) ;
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("currentDocId = " + currentDocId + " //  countPerPage" + countPerPage);
		try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //Snapshot snapshot;
            ApiFuture<DocumentSnapshot> future;
          
            future = db.collection("geometry_board").document(currentDocId).get();
            DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);




            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry_board").orderBy("reg_dt", Direction.ASCENDING).startAt(snapshot).limit(countPerPage).get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {

                Map<String, Object> data = document.getData();
                data.put("docId", document.getId());
                data.put("createdDate", data.get("reg_dt"));
                data.put("userAdress", data.get("user_adress"));
                data.put("userEmail", data.get("user_email"));
                data.put("userNm", data.get("user_name"));
            
                result.add(data);
            }
            Util.durationTime ("end", "CLOUD / < GET GEOMETRY BOARD LIST - SELECT > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < GET GEOMETRY BOARD LIST - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
		
		return result;
	}

    /**
    * @method 지오메트릭 데이터 가져오기
    * @param  id
    * @throws Exception
    */
    @Override
	public List<Map<String, Object>> getGeomData(long id) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / < GET GEOMETRY DATA LIST - SELECT > : ", 0, "Proceeding ::: " );
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry").whereEqualTo("id", id).get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                /* 
					※ 데이터 출력 Example 
                	System.out.println("id        : " + document.getId());
                */
                Map<String, Object> data = document.getData();
                /* 
                    String toDayFormat = Util.remakeDate(document.getLong("brddate"),1);
                    data.put("brddate", toDayFormat);
                */
                data.put("docId", document.getId());
                result.add(data);
            }
            Util.durationTime ("end", "CLOUD / < GET GEOMETRY DATA LIST - SELECT > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < GET GEOMETRY DATA LIST - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
		
		return result;
	}

    /**
    * @method 지오메트릭 데이터 삭제하기
    * @param  geomDeleteArr
    * @throws Exception
    */
    @Override
	public void deleteGeomdData(String[] geomDeleteArr) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / < SET GEOMETRY DATA - DELETE > : ", 0, "Proceeding ::: " );
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            for (String element : geomDeleteArr) {;
                
                ApiFuture<WriteResult> writeResult = db.collection("geometry").document(element).delete();
                System.out.println("Delete Time : " + writeResult.get().getUpdateTime() + "/ Delete Id : " + element);
            }
                        
            Util.durationTime ("end", "CLOUD / < SET GEOMETRY DATA - DELETE > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < SET GEOMETRY DATA - DELETE > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
	}

    /**
    * @method 지오메트릭 게시글 마지막인덱스 가져오기
    * @param  null
    * @throws Exception
    */
    @Override
	public long getLastIndex() throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / < SET BOARD LAST INDEX - SELECT > : ", 0, "Proceeding ::: " );
        
        long lastIdx = 0;
		
		try {
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry_board").orderBy("board_sq", Direction.ASCENDING).limitToLast(1).get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            if(documents.size() == 0){
                lastIdx = 0;
            }
            else{
                for (QueryDocumentSnapshot document : documents) {

                    Map<String, Object> data = document.getData();
                    
                    lastIdx =  Integer.parseInt((String) data.get("board_sq")) + 1;
                    LOG.info("BOARD LAST INDEX - " + data.get("board_sq"));

                    continue;
                }
            }

            Util.durationTime ("end", "CLOUD / < SET BOARD LAST INDEX - SELECT > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < SET BOARD LAST INDEX - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
		
		return lastIdx;
	}

    /**
    * @method 지오메트릭 데이터 삭제하기
    * @param  boardSq
    * @throws Exception
    */
    @Override
    public void setDeleteGeomData(String boardSq) throws Exception {
        long reqTime = Util.durationTime ("start", "CLOUD / < SET DELETE GEOMETRY DATA - DELETE >  : ", 0, "Proceeding ::: " );
        
        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry").whereEqualTo("id", Integer.parseInt(boardSq)).get();
            int deleted = 0;

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }

            Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY DATA - DELETE > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY DATA - DELETE > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
    }

    /**
    * @method 지오메트릭 게시글 총 갯수 가져오기
    * @param  paginationDto
    * @throws Exception
    */
    @Override
    public Integer getTotalCount(PaginationDto paginationDto) throws Exception {
        
        long reqTime = Util.durationTime ("start", "CLOUD / < GET BOARD TOTALCOUNT - SELECT > : ", 0, "Proceeding ::: " );
        
        Integer totalCount = 0;
        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            AggregateQuerySnapshot snapshot = db.collection("geometry_board").count().get().get();
            LOG.info("Get ToalCount - " + snapshot.getCount());

            Util.durationTime ("end", "CLOUD / < GET BOARD TOTALCOUNT - SELECT > : ", reqTime, "Complete ::: " );

           totalCount =  (int) snapshot.getCount();
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < GET BOARD TOTALCOUNT - SELECT > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return totalCount;
    }

    /**
    * @method 지오메트릭 글 삭제
    * @param  boardSq
    * @throws Exception
    */
    @Override
    public void setDeleteBoardData(String boardSq) throws Exception {
        long reqTime = Util.durationTime ("start", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", 0, "Proceeding ::: " );

        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry_board").whereEqualTo("board_sq", boardSq).get();
            int deleted = 0;
            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }

            Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
    }






    /**
    * @method 지오메트릭 글 삭제
    * @param  boardSq
    * @throws Exception
    */
    @Override
    public String getDocIdList(PaginationDto paginationDto) throws Exception {
        long reqTime = Util.durationTime ("start", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", 0, "Proceeding ::: " );

        System.out.println("★  ★ ★ ★ ★ ★★ ★ ★ ★ ★ ★ ★ -------------------> getCurrentPage()  =  " + paginationDto.getCurrentPage());
        Integer intCountPerPage = paginationDto.getCountPerPage();
        Integer intPageGroupSize = paginationDto.getBlockPage();
        Integer intTotalCount = paginationDto.getTotalCount();
        String strCurrentDocId = paginationDto.getCurrentPage().split("\\|")[0]; 
        String strCurrentPage = paginationDto.getCurrentPage().split("\\|")[1]; 
        String strCallType = paginationDto.getCurrentPage().split("\\|")[2];
        Integer intCurrentPage = Integer.valueOf(strCurrentPage);
        Integer callSize = (intCountPerPage * intPageGroupSize);// + intPageGroupSize;

        Integer intPageGroupStart = (((intCurrentPage ) / intPageGroupSize) * intPageGroupSize);// 보여질 페이지 시작점 EX> 3페이지 부터
        Integer intPageGroupEnd = (intPageGroupStart + intPageGroupSize) ;// 보여질 페이지 끝점 EX> 5페이지 까지

        //String[] docIdList = new String[intCountPerPage];
        List<String> docIdList = new ArrayList<String>();
        String strDocIdList = "";
        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            if(strCallType.equals("LAST")){

                
                
                Integer LastDocCnt = intTotalCount - (intPageGroupStart * intCountPerPage);
                /*
                 * 
                 * 마지막페이지를 계산해라  totalCOunt - intPageGroupStart * intCountPerPage 
                 * 한 숫자 만큼 뒤에서부터 호출하면댐 
                 */
                


                ApiFuture<DocumentSnapshot> future = db.collection("geometry_board").document(strCurrentDocId).get();
                DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);
                
                //스냅샷 호출 후 리스트 생성(JSON)
                ApiFuture<QuerySnapshot> query = db.collection("geometry_board").orderBy("reg_dt", Direction.ASCENDING).limitToLast(LastDocCnt).get();
                
                // future.get() blocks on document retrieval
                List<QueryDocumentSnapshot> documents = query.get().getDocuments();

                for (QueryDocumentSnapshot document : documents) {
                    
                    Map<String, Object> data = document.getData();

                    String boardSq =  (String) data.get("board_sq");
                    String docId = document.getId();

                    docIdList.add(docId);
                }

                strDocIdList = Arrays.toString(docIdList.toArray()).replaceAll("\\[","").replaceAll("\\]","");;
                
                return strDocIdList;
            }
            else if(!strCallType.equals("EACH")){
                if((intPageGroupEnd * intCountPerPage) >= intTotalCount){//마지막 페이지 일때
                    strDocIdList = paginationDto.getDocIdArr();
                }
                else{
                    ApiFuture<DocumentSnapshot> future = db.collection("geometry_board").document(strCurrentDocId).get();
                    DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);
                    
                    //스냅샷 호출 후 리스트 생성(JSON)
                    ApiFuture<QuerySnapshot> query = db.collection("geometry_board").orderBy("reg_dt", Direction.ASCENDING).startAt(snapshot).limit(callSize).get();

                    // future.get() blocks on document retrieval
                    List<QueryDocumentSnapshot> documents = query.get().getDocuments();

                    for (QueryDocumentSnapshot document : documents) {
                        
                        Map<String, Object> data = document.getData();

                        String boardSq =  (String) data.get("board_sq");
                        String docId = document.getId();

                        docIdList.add(docId);
                    }

                    strDocIdList = Arrays.toString(docIdList.toArray()).replaceAll("\\[","").replaceAll("\\]","");;
                    
                    
                }
                return strDocIdList;
                
            }
            else{
                strDocIdList = paginationDto.getDocIdArr();

                return strDocIdList;
            }
           
            //Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return strDocIdList;
    }

    /**
    * @method 지오메트릭 마지막 글 가져오기
    * @param  boardSq
    * @throws Exception
    */
    @Override
    public String getLastDoc(PaginationDto paginationDto) throws Exception {
        long reqTime = Util.durationTime ("start", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", 0, "Proceeding ::: " );

        String lastDocId = "";
        Integer countPerPage = paginationDto.getCountPerPage();
        String strCurrentPage = paginationDto.getCurrentPage();
        Integer intTotalCount = paginationDto.getTotalCount();
        Integer intPageGroupSize = paginationDto.getBlockPage();

        Integer intPageTotal = (intTotalCount -1) / intPageGroupSize;
        /*
         * 
         * 마지막 페이지 조건 두어서 구해보자... 
         * 1.페이지 토탈 * 카운트퍼 페이지  <= 토탈 카우트?? 몰라..
         * 
         */

        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry_board").orderBy("reg_dt", Direction.ASCENDING).limitToLast(countPerPage).get();

            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            
            /* 
            for (QueryDocumentSnapshot document : documents) {
                if(document.getId().equals(strCurrentPage)){
                    lastDocId = document.getId();
                }
            }
            */

            QueryDocumentSnapshot lastDoc = documents.get(0);
            lastDocId = lastDoc.getId();

            Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return lastDocId;
    }


    /**
    * @method 지오메트릭 첫번째 글 가져오기
    * @param  paginationDto
    * @throws Exception
    */
    @Override
    public String getFirstDoc(PaginationDto paginationDto) throws Exception {
        long reqTime = Util.durationTime ("start", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", 0, "Proceeding ::: " );

        String intCurrentPage = paginationDto.getCurrentPage();

        String firstDoc = "";

        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry_board").orderBy("reg_dt", Direction.ASCENDING).limit(1).get();

            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                
                firstDoc = document.getId() + "|0|FIRST";
            }

            if(intCurrentPage.equals("0")){
                paginationDto.setCurrentPage(firstDoc + "|0|FIRST");
            }
            Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return firstDoc;
    }

    /**
    * @method 지오메트릭 첫번째 글 가져오기
    * @param  paginationDto
    * @throws Exception
    */
    @Override
    public String getNextDoc(PaginationDto paginationDto) throws Exception {
        long reqTime = Util.durationTime ("start", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", 0, "Proceeding ::: " );
        String[] docIdArr = paginationDto.getDocIdArr().split(",");
        String intCurrentPage = paginationDto.getCurrentPage();
        String strDocIdArrLast = docIdArr[docIdArr.length-1].trim();
        String NextDocId = "";
        
        System.out.println(" -------------------------------------------------------> NextDoc = " + strDocIdArrLast);

        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<DocumentSnapshot> future = db.collection("geometry_board").document(strDocIdArrLast).get();
            DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);
                
            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry_board").orderBy("reg_dt", Direction.ASCENDING).startAfter(snapshot).limit(1).get();

            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                
                NextDocId = document.getId();
            }

            Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return NextDocId;
    }


    /**
    * @method 지오메트릭 첫번째 글 가져오기
    * @param  paginationDto
    * @throws Exception
    */
    @Override
    public String getPageGroupFirst(PaginationDto paginationDto) throws Exception {
        long reqTime = Util.durationTime ("start", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", 0, "Proceeding ::: " );

        String strCurrentPage = paginationDto.getCurrentPage();

        Integer countPerPage = paginationDto.getCountPerPage();
        Integer intTotalCount = paginationDto.getTotalCount();
        Integer intPageGroupSize = paginationDto.getBlockPage();

        String strCurrentDocId = paginationDto.getCurrentPage().split("\\|")[0]; 

        String pageGroupfirstDoc = "";

        try {
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry_board").orderBy("reg_dt", Direction.ASCENDING).limit(1).get();

            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                
                //pageGroupfirstDoc = document.getId() + "|0|FIRST";
            }

            Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / < SET DELETE GEOMETRY BOARD - DELETE > : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }

        return pageGroupfirstDoc;
    }
}