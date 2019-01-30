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
	
	public String getAcceptHeader() {
		return getHeaderValue(ACCEPT);
	}
	
	public String getHeaderValue(String header) {
		return getHeaderValue(req, this.headersKeysLowerCaseMap, header);
	}
	
	/* * * * * * * Metodi Statici * * * * * * * * */
	
	public static boolean containsXRequestedWith(HttpServletRequest req) {
		Map<String, String> lowerCaseHeadersKeysMap = getHeadersKeysLowerCaseMap(req);
		return containsXRequestedWith(lowerCaseHeadersKeysMap);
	}
	private static boolean containsXRequestedWith(Map<String, String> headersKeysLowerCaseMap) {
		return headersKeysLowerCaseMap.containsKey(X_REQUESTED_WITH.toLowerCase());
	}

	public static String getAcceptHeader(HttpServletRequest req) {
		return getHeaderValue(req, ACCEPT);
	}
	
	public static String getHeaderValue(HttpServletRequest req, String header) {
		return getHeaderValue(req, getHeadersKeysLowerCaseMap(req), header);
	}
	private static String getHeaderValue(HttpServletRequest req, Map<String, String> headersKeysLowerCaseMap, String header) {
		String headerActualKey = headersKeysLowerCaseMap.get(header.toLowerCase());
		String headerValue = req.getHeader(headerActualKey);
		return headerValue;
	}
	
	public static boolean isAcceptApplicationJson(HttpServletRequest req) {
		return isAcceptApplicationJson(req, getHeadersKeysLowerCaseMap(req));
	}
	private static boolean isAcceptApplicationJson(HttpServletRequest req, Map<String, String> headersKeysLowerCaseMap) {
		String headerValueLowecase = getAcceptHeader(req).toLowerCase();
		return headerValueLowecase.indexOf(APPLICATION_JSON.toLowerCase()) > -1;
	}

	public static Map<String, String> getHeadersKeysLowerCaseMap( HttpServletRequest req ) {
		Map<String, String> headersKeysLowerCaseMap = new HashMap<String, String>(); 
		Enumeration<String> headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String actualHeaderKey = headerNames.nextElement();
			String headerKeyLowerCase = actualHeaderKey.toLowerCase();
			headersKeysLowerCaseMap.put(headerKeyLowerCase, actualHeaderKey);
		}
		return headersKeysLowerCaseMap;
	}
}
