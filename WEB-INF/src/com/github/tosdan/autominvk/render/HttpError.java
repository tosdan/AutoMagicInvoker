package com.github.tosdan.autominvk.render;

import static com.github.tosdan.autominvk.render.Mime.TEXT_PLAIN;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.github.tosdan.autominvk.AutoMagicAction;
import com.github.tosdan.autominvk.AutoMagicHttpError;

public class HttpError implements AutoMagicRender {

	@Override
	public AutoMagicResponseObject getResponseObject( Object dataToRender, AutoMagicAction action, HttpServletRequest req, HttpServletResponse resp ) {

		String mime = action.getMimeType();
		
		AutoMagicHttpError error = (AutoMagicHttpError) dataToRender;
		
		resp.setStatus(error.getStatusCode());
		resp.setHeader("XX-ErrorMessage", error.getMessage());
		
		mime = StringUtils.defaultIfBlank(mime, TEXT_PLAIN);
		
		return new AutoMagicResponseObject(mime, error.getMessage());
	}

}
