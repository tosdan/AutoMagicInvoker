package com.github.tosdan.autominvk.rendering.render;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.autominvk.rendering.Mime;

public class JsonP extends Json {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonP.class);
	@Override
	protected String getResponseData(Object dataToRender, HttpServletRequest req) {
		String callback = req.getParameter("callback");
		LOGGER.debug("Callback : [{}]", callback);
		
		if (callback == null) {
			dataToRender = getExcptionMap(new IllegalArgumentException("Parametro callback mancante nella request."));
		}
		
		String respData = callback(gson.toJson(dataToRender), callback);
		
		return respData;
	}

	@Override
	protected String getRenderDefaultMime() {
		return Mime.APPLICATION_JAVASCRIPT;
	}
	
	protected String callback(String json, String callback) {
		return callback +"(" + json + ")";
	}

}
