package com.github.tosdan.autominvk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation che indica i metodi che possono essere invocati dal framework
 * @author Daniele
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IamInvokableAction {

	String render() default "";
	
	String reqMethod() default "";
	
	String alias() default "";
	
	String[] mapify() default "";
}
