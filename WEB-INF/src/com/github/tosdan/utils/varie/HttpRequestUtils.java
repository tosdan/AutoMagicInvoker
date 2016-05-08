package com.github.tosdan.utils.varie;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Daniele
 *
 */
public class HttpRequestUtils {
	
	public final static String[] sources = 
		{"X-Forwarded-For", "X-FORWARDED-FOR", "x-forwarded-for", "Proxy-Client-IP" , "X-Forwarded-Server", "X-Forwarded-Host", "X-Pounded-For", "Proxy-Client-IP" , "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

	
	/**
	 * 
	 * @param request
	 * @return
	 */
    public static String getClientIpAddr(HttpServletRequest request) {  
    	return getClientIpAddrSource(request).getIp();  
    }  
    
    /**
     * 
     * @param request
     * @return
     */
    public static ClientIP getClientIpAddrSource(HttpServletRequest request) { 
    	String ip, source;
    	
    	int i = 0;
    	do {
    		source = sources[i];
    		ip = request.getHeader(source);
    		i += 1;
    	} while (isInvalidIp(ip) && i < sources.length);
    	
    	if (isInvalidIp(ip)) {
    		ip = request.getRemoteAddr();
    	}
    	
    	return (new HttpRequestUtils())
    			.new ClientIP(ip, source);  
    }

    /**
     * 
     * @param ip
     * @return
     */
	private static boolean isInvalidIp(String ip) {
		return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
	}
    
	/**
	 * 
	 * @author Daniele
	 *
	 */
	public class ClientIP {
		public String getIp() 		{ return ip; }
		public String getSource() 	{ return source; }
		public ClientIP(String ip, String source) {
			this.ip = ip;
			this.source = source;
		}
		private String ip;
		private String source;
	}

	/**
	 * Processa il contenuto di una {@link HttpServletRequest} e genera una mappa con i parametri contenuti. 
	 * @param req
	 * @return
	 */
	public static String parseRequestBody(HttpServletRequest req) {
		StringBuilder body = new StringBuilder();
		String line;
		try {
			
			BufferedReader reqReader = req.getReader();
			while ( (line = reqReader.readLine()) != null ) {
				body.append(line);
			}
			reqReader.close();
			
		} catch ( IOException e ) {
			throw (RuntimeException) e.getCause();
		}
		return body.toString();
	}
}
