package com.github.tosdan.autominvk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotazione per contrassegnare le classi da analizzare alla ricerca di metodi che possono essere invocati dal framework
 * @author Daniele
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IamInvokable {
	/**
	 * Alias per il nome della classe
	 * @return
	 */
	String value() default "";
}
