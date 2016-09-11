package com.github.tosdan.autominvk;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestHeadersUtil {
	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_JSON = "application/json";
	private static final String X_REQUESTED_WITH = "X-Requested-With";
	private HttpServletRequest req;
	/**
	 * Mappa contenente (come valori) i nomi degli headers contenuti nella request 
	 * indicizzati in base al nome dell'header in lowercase (chiavi)
	 */
	private Map<String, String> headersKeysLowerCaseMap;

	public HttpRequestHeadersUtil(HttpServletRequest req) {
		this.headersKeysLowerCaseMap = getHeadersKeysLowerCaseMap(req);
		this.req = req;
	}
	
	public boolean containsXRequestedWith() {
		return containsXRequestedWith(this.headersKeysLowerCaseMap);
	}
	
	public boolean isAcceptApplicationJson() {
		return isAcceptApplicationJson(this.req, this.headersKeysLowerCaseMap);
	}
	
	/* * * * * * * Metodi Statici * * * * * * * * */
	
	public static boolean containsXRequestedWith(HttpServletRequest req) {
		Map<String, String> lowerCaseHeadersKeysMap = getHeadersKeysLowerCaseMap(req);
		return containsXRequestedWith(lowerCaseHeadersKeysMap);
	}
	private static boolean containsXRequestedWith(Map<String, String> headersKeysLowerCaseMap) {
		return headersKeysLowerCaseMap.containsKey(X_REQUESTED_WITH.toLowerCase());
	}
	
	public static boolean isAcceptApplicationJson(HttpServletRequest req) {
		return isAcceptApplicationJson(req, getHeadersKeysLowerCaseMap(req));
	}
	private static boolean isAcceptApplicationJson(HttpServletRequest req, Map<String, String> headersKeysLowerCaseMap) {
		String acceptHeaderActualKey = headersKeysLowerCaseMap.get(ACCEPT.toLowerCase());
		String acceptHeaderValue = req.getHeader(acceptHeaderActualKey);
		return acceptHeaderValue.toLowerCase().indexOf(APPLICATION_JSON.toLowerCase()) > -1;
	}

	private static Map<String, String> getHeadersKeysLowerCaseMap( HttpServletRequest req ) {
		Map<String, String> headersKeysLowerCaseMap = new HashMap<String, String>(); 
		@SuppressWarnings( "unchecked" )
		Enumeration<String> headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String actualHeaderKey = headerNames.nextElement();
			String headerKeyLowerCase = actualHeaderKey.toLowerCase();
			headersKeysLowerCaseMap.put(headerKeyLowerCase, actualHeaderKey);
		}
		return headersKeysLowerCaseMap;
	}
}
