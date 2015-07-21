package com.github.tosdan.autominvk.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.tosdan.autominvk.AutoMagicAction;

public interface AutoMagicRender {

	AutoMagicResponseObject getResponseObject(Object dataToRender, AutoMagicAction action, HttpServletRequest req, HttpServletResponse resp);
}
