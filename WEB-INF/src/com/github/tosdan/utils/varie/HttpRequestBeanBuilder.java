package com.github.tosdan.utils.varie;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

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
	public <T> T buildBeanFromRequest(Class<T> clazz, HttpServletRequest req, String requestBody, String gsonDateFormat) {
		return this.buildBeanFromRequest(clazz, req.getContentType(), requestBody, req.getQueryString(), req.getMethod(), gsonDateFormat);
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
	public <T> T buildBeanFromRequest(Class<T> clazz, String contentType, String requestBody, String queryString, String reqMethod, String gsonDateFormat) {
		
		T retval = null;
		
		logger.debug("Classe oggetto Bean richiesto: [{}]", clazz.getName());
		
		Gson gson = getGson(gsonDateFormat);
		
		logger.debug("Corpo della request: [{}]", requestBody);
		
		logger.debug("ContentType: [{}]", contentType);
		
		String json = null;
		
		if ("GET".equalsIgnoreCase(reqMethod) || "DELETE".equalsIgnoreCase(reqMethod)) {
			logger.debug("Parsing QueryString parameters...");

			Map<String, Object> requestParamsMap = QueryStringUtils.parse(queryString);
//			logger.debug("Parametri della request: {}", requestParamsMap);
			json = gson.toJson(requestParamsMap);
			
		} else if (StringUtils.containsIgnoreCase(contentType, APPLICATION_X_WWW_FORM_URLENCODED)) {
			logger.debug("Parsing Form POST parameters...");
			
			Map<String, Object> requestParamsMap = QueryStringUtils.parse(requestBody);
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

	private Gson getGson(String gsonDateFormat) {		
		GsonBuilder gsonBuilder = new GsonBuilder()
									.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
									.registerTypeAdapter(Float.class, new FloatTypeAdapter());
		if (gsonDateFormat != null) {
			gsonBuilder.setDateFormat(gsonDateFormat);
		}
		Gson gson = gsonBuilder.create();
		return gson;
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
