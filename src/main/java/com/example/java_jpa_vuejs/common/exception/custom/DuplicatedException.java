package com.example.java_jpa_vuejs.common.exception.custom;

public class DuplicatedException extends RuntimeException{
	
	public DuplicatedException(String msg, Throwable t) {
        super(msg, t);
    }

    public DuplicatedException(String msg) {
        super(msg);
    }

    public DuplicatedException() {
        super();
    }
}