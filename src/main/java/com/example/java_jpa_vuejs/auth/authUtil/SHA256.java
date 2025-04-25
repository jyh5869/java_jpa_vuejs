package com.example.java_jpa_vuejs.auth.authUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256{
		
	public String sha256(String plainText1) {
		String result = null;
		MessageDigest authmd;
		try {
			authmd = MessageDigest.getInstance("SHA-256");
			authmd.update(plainText1.getBytes("UTF-8"));
			result = byteToHexString(authmd.digest()).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String byteToHexString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
