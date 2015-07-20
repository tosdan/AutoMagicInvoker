package com.github.tosdan.utils.varie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * 
 * @author Daniele
 *
 */
public class HttpReuqestUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(HttpReuqestUtils.class);
	
	public final static String[] sources = 
		{"X-Forwarded-For", "X-FORWARDED-FOR", "x-forwarded-for", "Proxy-Client-IP" , "X-Forwarded-Server", "X-Forwarded-Host", "X-Pounded-For", "Proxy-Client-IP" , "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

	private static final String APPLICATION_JSON = "application/json";
	private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	
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
    	
    	return (new HttpReuqestUtils())
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
		StringBuilder json = new StringBuilder();
		String line;
		try {
			
			BufferedReader reqReader = req.getReader();
			while ( (line = reqReader.readLine()) != null ) {
				json.append(line);
			}
			reqReader.close();
			
		} catch ( IOException e ) {
			throw (RuntimeException) e.getCause();
		}
		return json.toString();
	}

	/**
	 * Processa il contenuto di una {@link HttpServletRequest} e genera un oggetto con i parametri contenuti.
	 * Supporta contentType <em>application/json</em> e <em>application/x-www-form-urlencoded</em>
	 * @param req
	 * @param clazz
	 * @return
	 */
	public static <T> T buildBeanFromRequest(HttpServletRequest req, Class<T> clazz) {
		
		T retval = null;
		
		logger.debug("Classe oggetto Bean richiesto: [{}]", clazz.getName());
		
		Gson gson = new GsonBuilder()
						.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
						.registerTypeAdapter(Float.class, new FloatTypeAdapter())
						.create();
		
		String requestBody = HttpReuqestUtils.parseRequestBody(req);
		logger.debug("Corpo della request: [{}]", requestBody);
		
		String contentType = req.getContentType();
		logger.debug("ContentType: [{}]", contentType);
		
		String json = null;
		
		if ("GET".equalsIgnoreCase(req.getMethod())) {
			logger.debug("Parseing QueryString parameters...");

			Map<String, Object> requestParamsMap = QueryStringUtils.parse(req.getQueryString());
//			logger.debug("Parametri della request: {}", requestParamsMap);
			json = gson.toJson(requestParamsMap);
			
		} else if (APPLICATION_X_WWW_FORM_URLENCODED.equalsIgnoreCase(contentType)) {
			logger.debug("Parseing Form POST parameters...");
			
			Map<String, Object> requestParamsMap = QueryStringUtils.parse(requestBody);
//			logger.debug("Parametri della request: {}", requestParamsMap);
			json = gson.toJson(requestParamsMap);
			
		} else if (APPLICATION_JSON.equalsIgnoreCase(contentType)) {

			json = requestBody;
		}
		
		logger.debug("Json intermedio: {}", json);
		
		logger.debug("Creating instance...");
		retval = gson.fromJson(json, clazz);
		
		return retval;
	}
	
	/**
	 * Type adapter per consentire a {@link Gson} di effettuare il parse di cifre decimali con separatore virgola ","
	 * @author Daniele
	 *
	 */
	public static class DoubleTypeAdapter extends TypeAdapter<Double> {
		@Override public Double read(JsonReader reader) throws IOException {
			Double retval = null;
            if (reader.peek() == JsonToken.NULL) { reader.nextNull(); }
            String stringValue = reader.nextString();
            try { 
            	Double value = Double.valueOf(stringValue.replace(",", "."));
                retval = value;
            } catch (NumberFormatException e) { }
            return retval;
        }
		@Override public void write(JsonWriter writer, Double value) throws IOException {
            if (value == null) { writer.nullValue(); }
            else { writer.value(value); }
        }
	}
	
	/**
	 * Type adapter per consentire a {@link Gson} di effettuare il parse di cifre decimali con separatore virgola ","
	 * @author Daniele
	 *
	 */
	public static class FloatTypeAdapter extends TypeAdapter<Float> {
		@Override public Float read(JsonReader reader) throws IOException {
			Float retval = null;
			if (reader.peek() == JsonToken.NULL) { reader.nextNull(); }
			String stringValue = reader.nextString();
			try { 
				Float value = Float.valueOf(stringValue.replace(",", "."));
				retval = value;
			} catch (NumberFormatException e) { }
			return retval;
		}
		@Override public void write(JsonWriter writer, Float value) throws IOException {
			if (value == null) { writer.nullValue(); }
			else { writer.value(value); }
		}
	}
}
