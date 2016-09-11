package com.github.tosdan.autominvk.apps;

import com.github.tosdan.autominvk.AutoMagicAction;
import com.github.tosdan.autominvk.AutoMagicInvokerActionNotFoundException;
import com.github.tosdan.autominvk.IamInvokable;
import com.github.tosdan.autominvk.IamInvokableAction;
import com.github.tosdan.autominvk.rendering.render.DefaultNull;

@IamInvokable
public class ActionNotFoundExceptionAmAction {
	
	private AutoMagicAction action;
	private AutoMagicInvokerActionNotFoundException e;

	@IamInvokableAction(render = DefaultNull.class)
	public Object get() {
		System.out.println(this.getClass().getName());
		System.out.println("ActionNotFoundExceptionAmAction.get(): " + action);
		return e;
	}
}
