package com.github.tosdan.autominvk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.tosdan.autominvk.rendering.AutoMagicRender;
import com.github.tosdan.autominvk.rendering.render.DefaultNull;

/**
 * Annotation che indica i metodi che possono essere invocati dal framework
 * @author Daniele
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IamInvokableAction {

	String mime() default "";
	
	String reqMethod() default "";
	
	String alias() default "";
	
	Class<? extends AutoMagicRender> render() default DefaultNull.class;
}
