package com.github.tosdan.autominvk;

public class AutoMagicHttpError {

	private String message;
	private int statusCode;
	public AutoMagicHttpError() {
	}
	public AutoMagicHttpError(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}	
	public String getMessage() {
		return message;
	}
	public void setMessage( String message ) {
		this.message = message;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode( int statusCode ) {
		this.statusCode = statusCode;
	}
}
