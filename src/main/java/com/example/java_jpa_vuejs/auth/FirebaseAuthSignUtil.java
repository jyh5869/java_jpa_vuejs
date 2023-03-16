package com.example.java_jpa_vuejs.auth;

import org.springframework.stereotype.Component;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class FirebaseAuthSignUtil {

    public static void getUserById(FirebaseAuth firebaseAuth, String uid) throws FirebaseAuthException {

        UserRecord userRecord = firebaseAuth.getUser(uid);

        // See the UserRecord reference doc for the contents of userRecord.

        System.out.println("Successfully fetched user data: " + userRecord.getUid());
        System.out.println(userRecord);
    }

    public static void createUser(FirebaseAuth firebaseAuth ) throws FirebaseAuthException {

        CreateRequest request = new CreateRequest()
            .setEmail("user@example.com")
            .setEmailVerified(false)
            .setPassword("secretPassword")
            .setPhoneNumber("+11234567890")
            .setDisplayName("John Doe")
            .setPhotoUrl("http://www.example.com/12345678/photo.png")
            .setDisabled(false);
      
        UserRecord userRecord = firebaseAuth.createUser(request);
        System.out.println("Successfully created new user: " + userRecord.getUid());
    }
}
