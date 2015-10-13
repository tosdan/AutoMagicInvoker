package com.github.tosdan.autominvk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;

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

	/**
	 * Content type da usare nella response.
	 * @return
	 */
	String mime() default "";
	/**
	 * Metodo della request accettato (GET, POST, DELETE, PUT, HEAD): se diverso da quanto specificato restituisce un errore.
	 * @return
	 */
	String reqMethod() default "";
	/**
	 * Alternativa cablata per identificare il metodo da chiamare. Invece di usare l'uri convenzionale formato dal 
	 * package più la classe più il nome del metodo, si può utilizzare un alias completamente arbitrario.
	 * @return
	 */
	String alias() default "";
	/**
	 * Formato data con cui verrà fatto il parse dei parametri che devono essere trasformati in oggetti {@link java.util.Date}.
	 * <br>Per i formati disponibili fare riferimento a {@link SimpleDateFormat}. 
	 * <br>Il formato di default è <code>dd/MM/yyyy</code>
	 * @return
	 */
	String gsonDateFormat() default "dd/MM/yyyy";
	/**
	 * Classe, che implementa {@link AutoMagicRender}, da usare per effettuare il render dell'oggetto 
	 * restituito dal metodo chiamato. 
	 * @return
	 */
	Class<? extends AutoMagicRender> render() default DefaultNull.class;
}
