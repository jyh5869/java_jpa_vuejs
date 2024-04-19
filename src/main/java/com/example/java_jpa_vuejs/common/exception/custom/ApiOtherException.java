package com.example.java_jpa_vuejs.common.exception.custom;

public class ApiOtherException extends RuntimeException{
	
	public ApiOtherException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public ApiOtherException(String msg) {
		super(msg);
	}
	
	public ApiOtherException() {
		super();
	}
	
}