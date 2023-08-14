package com.example.java_jpa_vuejs.auth.repositoryServiceImpl;

import com.common.Util;
import com.example.java_jpa_vuejs.auth.AuthenticationDto;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.auth.entity.Members;
import com.example.java_jpa_vuejs.auth.repositoryService.SignFirebaseService;
import com.example.java_jpa_vuejs.config.FirebaseConfiguration;


import org.springframework.stereotype.Service;

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


@Service("signFirebaseService")
@RequiredArgsConstructor
public class SignFirebaseServiceImpl implements SignFirebaseService {

    final private static Logger LOG = Logger.getGlobal();

	private final FirebaseConfiguration firebaseConfiguration;

	private final ModelMapper modelMapper;

	@Override
	public void userRegistration(JoinDto joinDto) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / USER REGISTRATION : ", 0, "Proceeding ::: " );

        try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();
            
            String lastIdx = String.valueOf(joinDto.getId());

            Map<String, Object> docData = new HashMap<String, Object>();
            docData.put("id", joinDto.getId());
            docData.put("email", joinDto.getEmail());
            docData.put("password", joinDto.getPassword());
            docData.put("name", joinDto.getName());
            docData.put("mobile", joinDto.getMobile());
            docData.put("nickname", joinDto.getNickname());
            docData.put("profile", joinDto.getProfile());
            docData.put("created_date", String.valueOf(ZonedDateTime.now()));
            docData.put("modified_date", String.valueOf(ZonedDateTime.now()));
            docData.put("delete_yn", "N");
            docData.put("auth_type", joinDto.getAuthType());
            
            ApiFuture<WriteResult> future = db.collection("user").document(lastIdx).set(docData);

            Util.durationTime ("end", "CLOUD / USER REGISTRATION : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "CLOUD / USER REGISTRATION : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
	}

    @Override
	public Members loginMember(LoginDto loginDto) throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / AUTH USER: ", 0, "Proceeding ::: " );
        Members member = new Members();
        JoinDto joinDto = new JoinDto();
        try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            String email = loginDto.getEmail();
            String password = loginDto.getPassword();
            //클라우드 로그인 부터 만들자!
            ApiFuture<QuerySnapshot> future = db.collection("user").whereEqualTo("email", email).whereEqualTo("password", password).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z");
            for (DocumentSnapshot document : documents) {

                Map<String, Object> docData = document.getData();

                ZonedDateTime createdDate = ZonedDateTime.parse(String.valueOf(docData.get("created_date")));
                ZonedDateTime modifiedDate = ZonedDateTime.parse(String.valueOf(docData.get("modified_date")));

                joinDto.setId((long) docData.get("id"));
                joinDto.setEmail((String) docData.get("email"));
                joinDto.setMobile((String) docData.get("mobile"));
                joinDto.setPassword((String) docData.get("password"));
                joinDto.setName((String) docData.get("name"));
                joinDto.setNickname((String) docData.get("nickname"));
                joinDto.setProfile((String) docData.get("profile"));
                joinDto.setCreatedDate(createdDate);
                joinDto.setModifiedDate(modifiedDate);
                joinDto.setDeleteYn((String) docData.get("delete_yn"));
                joinDto.setAuthType((String) docData.get("auth_type"));
                
                /* 문서를 객체와 매칭시켜 데이터를 가져 오는 Code(파라메터 명이 일치 해야 한다.)
                member = document.toObject(Members.class);
                */
            }

            Util.durationTime ("end", "CLOUD / AUTH USER: ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "CLOUD / AUTH USER: ", reqTime,  "Fail ::: " );
            e.printStackTrace();
        }

        return modelMapper.map(joinDto, Members.class);
	}

	@Override
	public List<Map<String, Object>> getList() throws Exception{

		long reqTime = Util.durationTime ("start", "CLOUD / GET LIST : ", 0, "Proceeding ::: " );
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
            Util.durationTime ("end", "CLOUD / GET LIST : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
			Util.durationTime ("end", "CLOUD / GET LIST : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
		
		return result;
	}

	@Override
	public void userModify(JoinDto joinDto) throws Exception  {
		long reqTime = Util.durationTime ("start", "CLOUD / UPDATE USER INFO : ", 0, "Proceeding ::: " );

        try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            String modifyIdx = String.valueOf(joinDto.getId());

            Map<String, Object> docData = new HashMap<String, Object>();
            docData.put("id", joinDto.getId());
            docData.put("email", joinDto.getEmail());
            docData.put("password", joinDto.getPassword());
            docData.put("name", joinDto.getName());
            docData.put("mobile", joinDto.getMobile());
            docData.put("nickname", joinDto.getNickname());
            docData.put("profile", joinDto.getProfile());
            docData.put("auth_type", joinDto.getAuthType());
            docData.put("modified_date", String.valueOf(ZonedDateTime.now()));
            docData.put("delete_yn", "Y");

            ApiFuture<WriteResult> future = db.collection("user").document(modifyIdx).set(docData, SetOptions.merge());
            WriteResult result = future.get();
            System.out.println(result);

            /* 
                ※ Doc Update 1``
                ApiFuture<WriteResult> future = db.collection("user").document().set(joinDto);

                ※ Doc Update 2
			    DocumentReference docRef = db.collection("user").document("DEWLD2Tb5vIvIl4YqtEX");
			    ApiFuture<WriteResult> future = docRef.update("capital", true);

                ※ Doc Update Result
                WriteResult result = future.get();

            */

            Util.durationTime ("end", "CLOUD / UPDATE USER INFO : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "CLOUD / UPDATE USER INFO : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
	}
	
    @Override
	public Members getUserInfo(LoginDto loginDto) throws Exception  {
		long reqTime = Util.durationTime ("start", "CLOUD / GET USER INFO : ", 0, "Proceeding ::: " );
        Members member = new Members();
        try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            String modifyIdx = String.valueOf(loginDto.getId());
        
            DocumentReference docRef = db.collection("user").document(modifyIdx);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                member = document.toObject(Members.class);
                LOG.info(member.toString());
            } 
            else {
                LOG.info("CLOUD / GET USER INFO : NO DATA");
            }

            Util.durationTime ("end", "CLOUD / GET USER INFO : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            Util.durationTime ("end", "CLOUD / GET USER INFO : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
       
        return modelMapper.map(member, Members.class);
	}

    @Override
	public void userDelete(JoinDto joinDto) throws Exception  {
		long reqTime = Util.durationTime ("start", "DELETE USER INFO", 0, "Proceeding ::: " );

        try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();

            String modifyIdx = String.valueOf(joinDto.getId());
            
            DocumentReference docRef = db.collection("user").document(modifyIdx);
			ApiFuture<WriteResult> future = docRef.update("delete_yn", "Y");
            WriteResult result = future.get();
            System.out.println(result);

            Util.durationTime ("end", "CLOUD / DELETE USER INFO : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            
            Util.durationTime ("end", "CLOUD / DELETE USER INFO : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
	}

    @Override
	public Integer idValidation(LoginDto loginDto) throws Exception  {
		long reqTime = Util.durationTime ("start", "CLOUD / CHECK ID DUPLICATION : ", 0, "Proceeding ::: " );

        Integer emailCnt = 0;
        try {     
            //파이어 베이스 초기화
            firebaseConfiguration.initializeFCM();
            Firestore db = FirestoreClient.getFirestore();
        
            ApiFuture<QuerySnapshot> future = db.collection("user").whereEqualTo("email", loginDto.getEmail()).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            emailCnt = documents.size();

            Util.durationTime ("end", "CLOUD / CHECK ID DUPLICATION : ", reqTime, "Complete ::: " );
        }
        catch (Exception e) {
            Util.durationTime ("end", "CLOUD / CHECK ID DUPLICATION : ", reqTime, "Fail ::: " );
            e.printStackTrace();
        }
       
        return emailCnt;
	}

}