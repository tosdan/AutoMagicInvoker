package com.github.tosdan.autominvk.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.github.tosdan.autominvk.AutoMagicAction;

public class Default implements AutoMagicRender {
	
	public Default() { }

	@Override
	public AutoMagicResponseObject getResponseObject(Object result, AutoMagicAction action, HttpServletRequest req, HttpServletResponse resp) {

		String mime = action.getMimeType();

		mime = StringUtils.defaultIfBlank(mime, Mime.TEXT_HTML);

		return new AutoMagicResponseObject(mime, result);
	}
}
