package com.github.tosdan.autominvk.render;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.tosdan.autominvk.AutoMagicAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json implements AutoMagicRender {

	protected Gson gson;
	
	public Json() {
		gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	@Override
	public AutoMagicResponseObject getResponseObject( Object dataToRender, AutoMagicAction action, HttpServletRequest req, HttpServletResponse resp ) {

		String mime = action.getMimeType();
		mime = StringUtils.defaultIfBlank(mime, Mime.TEXT_PLAIN);
		
		if (dataToRender instanceof Exception)  {
			dataToRender = getExcptionMap((Exception) dataToRender);
		}
		String respData = getResponseData(dataToRender, req);

		return new AutoMagicResponseObject(mime, respData);
	}

	protected String getResponseData(Object dataToRender, HttpServletRequest req) {
		String respData = gson.toJson(dataToRender);
		return respData;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	protected Map<String, Object> getExcptionMap(Exception e) {
		Map<String, Object> errMap = new HashMap<String, Object>();
		errMap.put("error", e.getMessage());
		errMap.put("stacktrace", ExceptionUtils.getStackTrace(e));
		return errMap;
	}

}
