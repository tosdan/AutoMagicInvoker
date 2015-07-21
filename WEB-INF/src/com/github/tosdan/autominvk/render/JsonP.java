package com.github.tosdan.autominvk.render;

import javax.servlet.http.HttpServletRequest;

public class JsonP extends Json {

	@Override
	protected String getResponseData(Object dataToRender, HttpServletRequest req) {
		String callback = req.getParameter("callback");
		if (callback == null) {
			dataToRender = getExcptionMap(new IllegalArgumentException("Parametro callback mancante nella request."));
		}
		
		String jsonP = callback(gson.toJson(dataToRender), callback);
		
		return jsonP;
	}
	
	protected String callback(String json, String callback) {
		return callback +"(" + json + ")";
	}

}
