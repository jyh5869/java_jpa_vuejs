package com.example.java_jpa_vuejs.geomBoard.repositoryServiceImpl;

import com.common.Util;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.auth.repositoryService.SignFirebaseService;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;
import com.example.java_jpa_vuejs.geomBoard.BoardDto;
import com.example.java_jpa_vuejs.geomBoard.repositoryService.BoardFirebaseService;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.cloud.FirestoreClient;


@Service("boardFirebaseService")
@RequiredArgsConstructor
public class BoardFirebaseServiceImpl implements BoardFirebaseService {

    final private static Logger LOG = Logger.getGlobal();

	private final FirebaseConfiguration firebaseConfiguration;

	private final ModelMapper modelMapper;

	@Override
	public void setGeomdData(long id, String geomPolygons) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / USER REGISTRATION : ", 0, "Proceeding ::: " );

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
                System.out.println("★ \u2605 \u2605 \u2605 \u2605 \u2605 \u2605");
                JSONObject obj = geomArray.getJSONObject(i);
                System.out.println(obj.toString());
                System.out.println("○\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB");

                String dataType = obj.getString("type");
                System.out.println("dataType(" + i + "): " + dataType);

                String geometry = obj.getString("geometry");
                System.out.println("geometry(" + i + "): " + geometry);
                System.out.println("★ \u2605 \u2605 \u2605 \u2605 \u2605 \u2605");
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
                System.out.println("○\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB = " + state);
                if(state.equals("insert")){//인서트
                    ApiFuture<WriteResult> future = db.collection("geometry").document().set(docData);
                }
                else if(state.equals("update")){//업데이트
                    String docId = obj.getString("id");
                    ApiFuture<WriteResult> future = db.collection("geometry").document(docId).set(docData);
                }
            }    
                
            Util.durationTime ("end", "CLOUD / USER REGISTRATION : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "CLOUD / USER REGISTRATION : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
	}


    @Override
	public List<Map<String, Object>> getGeomData(long id) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / GET LIST : ", 0, "Proceeding ::: " );
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("geometry").orderBy("reg_dt", Direction.DESCENDING).get();
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
            Util.durationTime ("end", "CLOUD / GET LIST : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / GET LIST : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
		
		return result;
	}
}