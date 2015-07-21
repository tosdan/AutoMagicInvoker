package com.github.tosdan.autominvk.rendering.render;

import static com.github.tosdan.autominvk.rendering.Mime.TEXT_PLAIN;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.autominvk.AutoMagicAction;
import com.github.tosdan.autominvk.AutoMagicHttpError;
import com.github.tosdan.autominvk.rendering.AutoMagicRender;
import com.github.tosdan.autominvk.rendering.AutoMagicResponseObject;

public class HttpError implements AutoMagicRender {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpError.class);
	
	@Override
	public AutoMagicResponseObject getResponseObject( Object dataToRender, AutoMagicAction action, HttpServletRequest req, HttpServletResponse resp ) {

		String mime = action.getMimeType();
		mime = StringUtils.defaultIfBlank(mime, TEXT_PLAIN);
		LOGGER.debug("Mime: [{}]", mime);
		
		AutoMagicHttpError error = (AutoMagicHttpError) dataToRender;
		LOGGER.debug("Oggetto AutoMagicHttpError: [{}]", error);
		
		String respData = error.getMessage();
		
		resp.setStatus(error.getStatusCode());
		resp.setHeader("XX-ErrorMessage", respData);
		
		LOGGER.debug("Dati renderizzati:\n{}", respData);
		
		return new AutoMagicResponseObject(mime, respData);
	}

}
