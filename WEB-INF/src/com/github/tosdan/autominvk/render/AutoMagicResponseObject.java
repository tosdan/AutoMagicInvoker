package com.github.tosdan.autominvk.render;

public class AutoMagicResponseObject {
	private String mimeType;
	private Object responseObject;
	public AutoMagicResponseObject(String mimeType, Object responseObject) {
		this.mimeType = mimeType;
		this.responseObject = responseObject;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType( String mimeType ) {
		this.mimeType = mimeType;
	}
	public Object getResponseObject() {
		return responseObject;
	}
	public void setResponseObject( Object responseObject ) {
		this.responseObject = responseObject;
	}
}
