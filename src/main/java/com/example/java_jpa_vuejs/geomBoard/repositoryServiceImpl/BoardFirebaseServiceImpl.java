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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.cloud.FirestoreClient;

import lombok.RequiredArgsConstructor;


@Service("boardFirebaseService")
@RequiredArgsConstructor
public class BoardFirebaseServiceImpl implements BoardFirebaseService {

    final private static Logger LOG = Logger.getLogger(BoardFirebaseServiceImpl.class.getName());

	private final FirebaseConfiguration firebaseConfiguration;


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

                String dataType = obj.getString("type");
                String geometry = obj.getString("geometry");
                String properties = obj.getString("properties");
                
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
            
            String state = boardDTO.getActionType();

            if(state.equals("insert")){//인서트
                docData.put("reg_dt", regDt);
                ApiFuture<WriteResult> future = db.collection("geometry_board").document().set(docData);
            }
            else if(state.equals("update")){//업데이트
                docData.put("reg_dt", boardDTO.getCreatedDate());
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

		String currentDocId = String.valueOf(paginationDto.getCurrentPage().split("\\|")[0]);// 현재 도큐멘트ID
        String strCollectionNm = paginationDto.getCollectionNm();//도큐먼트를 가져올 컬렉션 명
        String strOrderbyCol = paginationDto.getOrderbyCol();//도큐먼트를 가져올 컬렉션의 정렬 컬럼

        Integer intCountPerPage = Integer.valueOf(paginationDto.getCountPerPage());// 페이지마다 보여줄 게시물 수

        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            ApiFuture<DocumentSnapshot> future;
          
            future = db.collection(strCollectionNm).document(currentDocId).get();
            DocumentSnapshot snapshot = future.get(30, TimeUnit.SECONDS);

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection(strCollectionNm).orderBy(strOrderbyCol, Direction.ASCENDING).startAt(snapshot).limit(intCountPerPage).get();
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
		
		try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            for (String element : geomDeleteArr) {;
                
                ApiFuture<WriteResult> writeResult = db.collection("geometry").document(element).delete();

                LOG.info("Delete Time : " + writeResult.get().getUpdateTime() + " - Delete Id : " + element);
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

            ApiFuture<QuerySnapshot> query = db.collection("geometry").whereEqualTo("id", Integer.parseInt(boardSq)).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            
            int deleted = 0;
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

            ApiFuture<QuerySnapshot> query = db.collection("geometry_board").whereEqualTo("board_sq", boardSq).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            int deleted = 0;
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
}