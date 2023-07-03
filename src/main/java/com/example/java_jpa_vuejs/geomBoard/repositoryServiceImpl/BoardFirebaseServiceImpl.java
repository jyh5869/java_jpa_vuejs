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
	public void setBoardData(BoardDto boardDTO) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / USER REGISTRATION : ", 0, "Proceeding ::: " );

        try {
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();
            
            
            String lastIdx = String.valueOf(boardDTO.getId());

            String geomData = URLDecoder.decode(boardDTO.getArrpolygon().toString(), "UTF-8");

            JSONObject jObject = new JSONObject(geomData);

            String features = jObject.getString("features");

            JSONArray geomArray = new JSONArray(features);

            for(int i = 0 ; i < geomArray.length(); i ++){
                System.out.println("★ \u2605 \u2605 \u2605 \u2605 \u2605 \u2605");
                JSONObject obj = geomArray.getJSONObject(i);
                System.out.println(obj.toString());
                System.out.println("○\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB");

                String type = obj.getString("type");
                System.out.println("type(" + i + "): " + type);

                String geometry = obj.getString("geometry");
                System.out.println("geometry(" + i + "): " + geometry);
                System.out.println("★ \u2605 \u2605 \u2605 \u2605 \u2605 \u2605");


                SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd a hh:mm:ss.SS");
                Map<String, Object> docData = new HashMap<String, Object>();

                docData.put("id", boardDTO.getId());
                docData.put("geom_type", type);
                docData.put("geom_value", geometry);
                docData.put("reg_dt", dayTime.toString());
                
                ApiFuture<WriteResult> future = db.collection("geom_board_list").document().set(docData);
            }

            //System.out.println("features: " + features); 

            /* */ 
            
               
            Util.durationTime ("end", "CLOUD / USER REGISTRATION : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "CLOUD / USER REGISTRATION : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
	}

}