package com.github.tosdan.autominvk;

import com.github.tosdan.autominvk.rendering.render.DefaultNull;

@IamInvokable("/" + ActionNotFoundExceptionAmAction.ACTION_NOT_FOUND_EXCEPTION)
public class ActionNotFoundExceptionAmAction {
	
	public static final String ACTION_NOT_FOUND_EXCEPTION = "actionNotFoundException";
	
	@SuppressWarnings( "unused" )
	private AutoMagicAction action;
	private AutoMagicInvokerActionNotFoundException e;

	@IamInvokableAction(render = DefaultNull.class)
	public Object get() {
		return e;
	}

	public static String getActionId() {
		return ACTION_NOT_FOUND_EXCEPTION;
	}
}
