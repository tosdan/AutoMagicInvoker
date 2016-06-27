package com.github.tosdan.autominvk.rendering.render;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.autominvk.AutoMagicAction;
import com.github.tosdan.autominvk.rendering.AutoMagicRender;
import com.github.tosdan.autominvk.rendering.AutoMagicResponseObject;
import com.github.tosdan.autominvk.rendering.GsonFactory;
import com.github.tosdan.autominvk.rendering.Mime;
import com.github.tosdan.autominvk.rendering.RenderOptions;
import com.google.gson.Gson;

public class Json2 implements AutoMagicRender {

	protected Gson gson;
	private static final Logger LOGGER = LoggerFactory.getLogger(Json2.class);
	
	public Json2() {}

	private void setGson(RenderOptions renderOptions) {
		this.gson = GsonFactory.getGson(renderOptions);
	}
	
	@Override
	public AutoMagicResponseObject getResponseObject( Object dataToRender, AutoMagicAction action, HttpServletRequest req, HttpServletResponse resp ) {

		setGson(action.getRenderOptions());
		
		String mime = action.getMimeType();
		mime = StringUtils.defaultIfBlank(mime, getRenderDefaultMime());
		
		LOGGER.debug("Mime: [{}]", mime);
		
		if (dataToRender instanceof Exception)  {
			dataToRender = getExcptionMap((Exception) dataToRender);
		}
		
		String respData = getResponseData(dataToRender, req);
		LOGGER.debug("Dati renderizzati:\n{}", cutRespData(respData));
		LOGGER.trace("Dati renderizzati:\n{}", respData);

		return new AutoMagicResponseObject(mime, respData);
	}

	private String cutRespData( String respData ) {
		return (respData != null && respData.length() > 80) 
				? respData.substring(0, 80)+"...\n[ full content in TRACE log]" 
				: respData;
	}

	protected String getRenderDefaultMime() {
		return Mime.TEXT_PLAIN;
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
		errMap.put("successful", false);
		errMap.put("msg", e.getMessage());
		errMap.put("exception", e.getMessage());
		errMap.put("error", e.getMessage());
		errMap.put("stacktrace", ExceptionUtils.getStackTrace(e));
		return errMap;
	}

}
