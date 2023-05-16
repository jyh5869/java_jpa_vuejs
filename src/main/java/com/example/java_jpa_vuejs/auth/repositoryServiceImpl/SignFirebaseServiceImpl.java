package com.example.java_jpa_vuejs.auth.repositoryServiceImpl;

import com.common.Util;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.auth.entity.Phones;
import com.example.java_jpa_vuejs.auth.repositoryJPA.MemberRepository;
import com.example.java_jpa_vuejs.auth.repositoryJPA.PhonesRepository;
import com.example.java_jpa_vuejs.auth.repositoryService.SignFirebaseService;
import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;
import com.example.java_jpa_vuejs.util.DuplicatedException;
import com.example.java_jpa_vuejs.util.ForbiddenException;
import com.example.java_jpa_vuejs.util.UserNotFoundException;
import com.example.java_jpa_vuejs.validation.Empty;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.firestore.Query.Direction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;



@Service("signFirebaseService")
@RequiredArgsConstructor
public class SignFirebaseServiceImpl implements SignFirebaseService {

	private final FirebaseConfiguration firebaseConfiguration;

	private final ModelMapper modelMapper;

	@Override
	public void userRegistration(JoinDto joinDto) throws Exception{

		long reqTime = Util.durationTime ("start", "JPA 테스트", 0, "Proceeding" );

        try {     

            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();
            
            ApiFuture<WriteResult> future = db.collection("user").document().set(joinDto);

            Util.durationTime ("end", "Update time : ", reqTime, "Complete" );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "JPA 테스트", reqTime, "Fail" );
            e.printStackTrace();
        }
	}

	@Override
	public List<Map<String, Object>> getList() throws Exception{

		long reqTime = Util.durationTime ("start", "JPA 테스트", 0, "Proceeding" );
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            //스냅샷 호출 후 리스트 생성(JSON)
            ApiFuture<QuerySnapshot> query = db.collection("board").orderBy("brddate", Direction.DESCENDING).limit(10).get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            

            for (QueryDocumentSnapshot document : documents) {
                /* 
					※ 데이터 출력 Example 
                	System.out.println("id        : " + document.getId());
                */

                String toDayFormat = Util.remakeDate(document.getLong("brddate"),1);
                
                Map<String, Object> data = document.getData();
                data.put("brddate", toDayFormat);

                result.add(data);
            }
        }
        catch (Exception e) {
			Util.durationTime ("end", "JPA 테스트", reqTime, "Fail" );
            e.printStackTrace();
        }
		Util.durationTime ("end", "JPA 테스트", reqTime, "Complete" );
		
		return result;
	}

	@Override
	public void userModify(JoinDto joinDto) throws Exception  {
		long reqTime = Util.durationTime ("start", "JPA 테스트", 0, "Proceeding" );

        try {     

            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();
            
            //ApiFuture<WriteResult> future = db.collection("user").document().set(joinDto);

            Util.durationTime ("end", "Update time : ", reqTime, "Complete" );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "JPA 테스트", reqTime, "Fail" );
            e.printStackTrace();
        }
	}
	

	
}