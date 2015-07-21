package com.github.tosdan.autominvk.rendering;

public class AutoMagicResponseObject {
	private String mimeType;
	private Object responseObject;
	private String charset;
	public AutoMagicResponseObject(String mimeType, Object responseObject) {
		this.mimeType = mimeType;
		this.responseObject = responseObject;
		this.charset = "";
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
	public String getCharset() {
		return charset;
	}
	public void setCharset( String charset ) {
		this.charset = charset;
	}
}
