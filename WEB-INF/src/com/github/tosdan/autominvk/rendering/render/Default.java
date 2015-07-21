package com.github.tosdan.autominvk.rendering.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.autominvk.AutoMagicAction;
import com.github.tosdan.autominvk.rendering.AutoMagicRender;
import com.github.tosdan.autominvk.rendering.AutoMagicResponseObject;
import com.github.tosdan.autominvk.rendering.Mime;

public class Default implements AutoMagicRender {

	private static final Logger LOGGER = LoggerFactory.getLogger(Default.class);
	public Default() { }

	@Override
	public AutoMagicResponseObject getResponseObject(Object result, AutoMagicAction action, HttpServletRequest req, HttpServletResponse resp) {

		String mime = action.getMimeType();

		mime = StringUtils.defaultIfBlank(mime, Mime.TEXT_HTML);

		LOGGER.debug("Mime: [{}]", mime);
		LOGGER.debug("Dati renderizzati:\n{}", result);
		
		return new AutoMagicResponseObject(mime, result);
	}
}
