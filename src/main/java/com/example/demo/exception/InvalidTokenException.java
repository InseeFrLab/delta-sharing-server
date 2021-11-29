package com.example.demo.exception;

public class InvalidTokenException extends Exception { 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidTokenException(String errorMessage) {
        super(errorMessage);
    }
}