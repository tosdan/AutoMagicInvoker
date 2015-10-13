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
	public final static String GSON_DATE_FORMAT = "HttpReuqestUtils.gsonDateFormat";
	private String gsonDateFormat;

	public HttpRequestBeanBuilder() {
		this.gsonDateFormat = GSON_DATE_FORMAT;
	}

	public HttpRequestBeanBuilder setGsonDateFormat( String gsonDateFormat ) {
		this.gsonDateFormat = gsonDateFormat;
		return this;
	}
	
	/**
	 * 
	 * Processa il contenuto di una {@link HttpServletRequest} e, con i parametri contenuti, genera un oggetto.
	 * Supporta contentType <em>application/json</em> e <em>application/x-www-form-urlencoded</em>
	 * @param req
	 * @param clazz
	 * @return
	 */
	public <T> T buildBeanFromRequest(HttpServletRequest req, Class<T> clazz) {
		
		T retval = null;
		
		logger.debug("Classe oggetto Bean richiesto: [{}]", clazz.getName());
		
		Gson gson = getGson(req);
		
		String requestBody = HttpReuqestUtils.parseRequestBody(req);
		logger.debug("Corpo della request: [{}]", requestBody);
		
		String contentType = req.getContentType();
		logger.debug("ContentType: [{}]", contentType);
		
		String json = null;
		
		if ("GET".equalsIgnoreCase(req.getMethod()) || "DELETE".equalsIgnoreCase(req.getMethod())) {
			logger.debug("Parsing QueryString parameters...");

			Map<String, Object> requestParamsMap = QueryStringUtils.parse(req.getQueryString());
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

	private Gson getGson( HttpServletRequest req ) {
		Gson gson = new GsonBuilder()
						.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
						.registerTypeAdapter(Float.class, new FloatTypeAdapter())
						.setDateFormat( this.getGsonDateFormat(req) )
						.create();
		return gson;
	}

	private String getGsonDateFormat(HttpServletRequest req) {		
		String gsonDateFormat = this.gsonDateFormat;
		if (gsonDateFormat == null) {
			gsonDateFormat = req.getParameter(GSON_DATE_FORMAT);
			if (gsonDateFormat == null ) {
				gsonDateFormat = (String) req.getAttribute(GSON_DATE_FORMAT);
				
				if (gsonDateFormat == null || this.gsonDateFormat.isEmpty()) {
					gsonDateFormat = GSON_DATE_FORMAT;
				}
			}
		}
		return gsonDateFormat;
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
