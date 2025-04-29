package com.example.java_jpa_vuejs.auth.authUtil;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;

public class SEED_ENC {

	public String decrypt(String str , String key) {
		
    	byte[] Key = new String(key).getBytes();
        byte[] SEEED_IV = new String("SASLTMETRCIAS000").getBytes();
        
        Decoder decoder1 = Base64.getDecoder(); 
        byte[] SEED_Key = decoder1.decode(Key);  
		

		Decoder decoder = java.util.Base64.getDecoder();
		byte[] msg = decoder.decode(str);

		String result = "";
		byte[] dec = null;

		try {
			dec = KISA_SEED_CBC.SEED_CBC_Decrypt(SEED_Key, SEEED_IV, msg, 0, msg.length);
			result = new String(dec, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
