package com.github.tosdan.autominvk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IamInvokableAction {

	String method() default "";
	
	String alias() default "";
	
	String[] mapify() default "";
}
