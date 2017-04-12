package com.github.tosdan.autominvk;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.autominvk.rendering.GsonFactory;
import com.github.tosdan.autominvk.rendering.RenderOptions;
import com.google.common.base.ThrowablesRevive;
import com.google.gson.Gson;

public class HttpRequestBeanBuilder {
	private final static Logger logger = LoggerFactory.getLogger(HttpRequestBeanBuilder.class);
	public static final String DEFAULT_GSON_DATE_FORMAT = "dd/MM/yyyy";
	private static final String APPLICATION_JSON = "application/json";
	private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public final static String GSON_DATE_FORMAT = HttpRequestBeanBuilder.class.getSimpleName()+".gsonDateFormat";

	public HttpRequestBeanBuilder() {}

	/**
	 * Processa i parametri della request (dal body o dalla querystring) e genera e popola un oggetto con tali parametri.
	 * Supporta contentType <em>application/json</em> e <em>application/x-www-form-urlencoded</em>
	 * @param clazz Classe dell'oggetto da generare
	 * @param req Oggetto rappresentante la chiamata http
	 * @param requestBody Corpo della chiamata http, se presente. Può essere null (di norma non è presente per GET, DELETE e HEAD). 
	 * @return
	 */

	public <T> T buildBeanFromRequest(Class<T> clazz, HttpServletRequest req, String requestBody) {
		return this.buildBeanFromRequest(clazz, req, requestBody, null);
	}
	
	/**
	 * Processa i parametri della request (dal body o dalla querystring) e genera e popola un oggetto con tali parametri.
	 * Supporta contentType <em>application/json</em> e <em>application/x-www-form-urlencoded</em>
	 * @param clazz Classe dell'oggetto da generare
	 * @param req Oggetto rappresentante la chiamata http
	 * @param requestBody Corpo della chiamata http, se presente. Può essere null (di norma non è presente per GET, DELETE e HEAD).
	 * @param gsonDateFormat Formato data che Gson utilizzerà per fare il parse dei parametri destinati a diventare un oggetto {@link java.util.Date}
	 * @return
	 */
	public <T> T buildBeanFromRequest(Class<T> clazz, HttpServletRequest req, String requestBody, RenderOptions renderOptions) {
		return this.buildBeanFromRequest(clazz, req.getContentType(), requestBody, req.getQueryString(), req.getMethod(), renderOptions);
	}
	
	/**
	 * Processa i parametri della request (dal body o dalla querystring) e genera e popola un oggetto con tali parametri.
	 * Supporta contentType <em>application/json</em> e <em>application/x-www-form-urlencoded</em>
	 * @param clazz Classe dell'oggetto da generare
	 * @param contentType Content type della chiamata http
	 * @param requestBody Corpo della chiamata http, se presente. Può essere null (di norma non è presente per GET, DELETE e HEAD).
	 * @param queryString Stringa dei parametri della chiamata http
	 * @param reqMethod Metodo della chiamata http
	 * @return
	 */
	public <T> T buildBeanFromRequest(Class<T> clazz, String contentType, String requestBody, String queryString, String reqMethod) {
		return this.buildBeanFromRequest(clazz, contentType, requestBody, queryString, reqMethod, null);
	}
	
	/**
	 * Processa i parametri della request (dal body o dalla querystring) e genera e popola un oggetto con tali parametri.
	 * Supporta contentType <em>application/json</em> e <em>application/x-www-form-urlencoded</em>
	 * @param clazz Classe dell'oggetto da generare
	 * @param contentType Content type della chiamata http
	 * @param requestBody Corpo della chiamata http, se presente. Può essere null (di norma non è presente per GET, DELETE e HEAD).
	 * @param queryString Stringa dei parametri della chiamata http
	 * @param reqMethod Metodo della chiamata http
	 * @param gsonDateFormat Formato data che Gson utilizzerà per fare il parse dei parametri destinati a diventare un oggetto {@link java.util.Date}
	 * @return
	 */
	public <T> T buildBeanFromRequest(Class<T> clazz, String contentType, String requestBody, String queryString, String reqMethod, RenderOptions renderOptions) {
		
		T retval = null;
		
		logger.debug("Classe oggetto Bean richiesto: [{}]", clazz.getName());
		
		Gson gson = getGson(renderOptions);
		
		logger.debug("Corpo della request: [{}]", requestBody);
		
		logger.debug("ContentType: [{}]", contentType);
		
		String json = null;
		
		if ("GET".equalsIgnoreCase(reqMethod) || "DELETE".equalsIgnoreCase(reqMethod)) {
			logger.debug("Parsing QueryString parameters...");

			Map<String, Object> requestParamsMap = URLEncodedParamsParser.parse(queryString, gson);
//			logger.debug("Parametri della request: {}", requestParamsMap);
			json = gson.toJson(requestParamsMap);
			
		} else if (StringUtils.containsIgnoreCase(contentType, APPLICATION_X_WWW_FORM_URLENCODED)) {
			logger.debug("Parsing x-www-form-urlencoded POST parameters...");
			
			Map<String, Object> requestParamsMap = URLEncodedParamsParser.parse(requestBody, gson);
			logger.debug("Parametri della request: {}", requestParamsMap);
			json = gson.toJson(requestParamsMap);
			
		} else if (StringUtils.containsIgnoreCase(contentType, APPLICATION_JSON)) {

			json = requestBody;
			
		} else {
			logger.warn("Impossibile interpretare i parametri della request!");
		}
		
		logger.debug("Json intermedio: {}", json);
		
		logger.debug("Creating instance...");
		retval = gson.fromJson(json, clazz);
		
		return retval;
	}

	private Gson getGson(RenderOptions renderOptions) {		
		return GsonFactory.getGson(renderOptions);
	}
	

	/**
	 * Processa il contenuto di una {@link HttpServletRequest} e restituisce il corpo della richiesta HTTP. 
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
			ThrowablesRevive.propagate(e);
		}
		return body.toString();
	}
}
