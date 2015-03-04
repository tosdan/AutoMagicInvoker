package com.github.tosdan.autominvk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoMagicMethodInvoker {

	private static Logger logger = LoggerFactory.getLogger(AutoMagicMethodInvoker.class);
	private AutoMagicClassCrawler crawler;

	public AutoMagicMethodInvoker(AutoMagicClassCrawler crawler) {
		this.crawler = crawler;
	}

	/**
	 * 
	 * @param amAction
	 * @return
	 */
	public Object invoke(AutoMagicAction amAction) {
		return invoke(amAction, null, null);
	}
	
	/**
	 * 
	 * @param amAction
	 * @param req
	 * @param ctx
	 * @return
	 */
	public Object invoke(AutoMagicAction amAction, HttpServletRequest req, ServletContext ctx) {
		Object retval = null;
		String 	methodName = amAction.getMethodName(),
				actionName = amAction.getActionName(),
				httpMethod = null;
		
		if (req != null) {
			httpMethod = req.getMethod();
		}
		
		try {
			Object instance = getInstance(actionName);
			injectParams(instance, req, ctx);
			
			if (methodName == null || methodName.isEmpty()) {
				methodName = "execute";
			}
			
			Method method = getMethod(methodName, httpMethod, instance.getClass());
			Object[] args = null;
			retval = method.invoke(instance, args);
			retval = postProcess(retval, method, actionName);
			
		} catch (NoSuchMethodException e) {
			String msg = "Il metodo ["+methodName+"] non è stato trovato nell'azione ["+actionName+"].";
			logger.error(msg, e);
			throw new AutoMagicInvokerException(msg, e);
		} catch (IllegalAccessException e) {
			String msg = "Impossibile accedere al metodo ["+methodName+"] dell'azione ["+actionName+"].";
			logger.error(msg, e);
			throw new AutoMagicInvokerException(msg, e);
		} catch (InvocationTargetException e) {
			String msg = "Impossibile eseguire il metodo ["+methodName+"] dell'azione ["+actionName+"].";
			logger.error("{}", e);
			logger.error(msg, e.getCause());
			throw new AutoMagicInvokerException(msg, e.getCause());
		}
		return retval;
	}

	private Object postProcess(Object input, Method method, String actionName) {
		Object retval = input;
		IamInvokableAction ann = method.getAnnotation(IamInvokableAction.class);
		String[] mapify = ann.mapify();
		logger.debug("Campi da utilizzare per Mappificazione = {}", Arrays.asList(mapify));
		if (hasValue(mapify)) {
			try {
				retval = MapMaker.getMap(retval, mapify);
			} catch (IllegalArgumentException e) {
				String msg = "Errore di accesso ai campi in fase di Post Process per il metodo ["+method.getName()+"] dell'azione ["+actionName+"].";
				logger.error(msg, e);
				throw new AutoMagicInvokerException(msg, e.getCause());
				
			} catch (IllegalAccessException e) {
				String msg = "Errore in fase di Post Process per il metodo ["+method.getName()+"] dell'azione ["+actionName+"].";
				logger.error(msg, e);
				throw new AutoMagicInvokerException(msg, e.getCause());
			}
		}
		return retval;
	}

	private boolean hasValue( String[] mapify ) {
		return mapify.length > 0 && !mapify[0].isEmpty();
	}

	/**
	 * 
	 * @param methodName
	 * @param httpMethod
	 * @param clazz
	 * @return
	 * @throws NoSuchMethodException
	 */
	private Method getMethod(String methodName, String httpMethod, Class<?> clazz) throws NoSuchMethodException {
		Method retval = null;
		IamInvokableAction ann;
		String errMsg = null;
		
		Method[] methods = clazz.getDeclaredMethods();
		
		for(Method m : methods) {
			boolean mwthodFound = m.getName().equals(methodName);
			
			if (mwthodFound) {
				int parametersLentgh = m.getParameterTypes().length;
				
				if (parametersLentgh > 0) {
					errMsg = "Metodo ["+methodName+"] trovato. I metodi non possono avere parametri.";
					
				} else {
					ann = m.getAnnotation(IamInvokableAction.class);
					boolean annotationFound = ann != null;
					
					if (annotationFound) {
						String annMethod = ann.method();
						boolean noHttpMethodOrEquals = annMethod.isEmpty() || httpMethod.equalsIgnoreCase(annMethod);
						
						if (noHttpMethodOrEquals) {
							retval = m;
							errMsg = null;
							break;
							
						} else {
							errMsg = "Metodo ["+methodName+"] trovato. Il metodo è configurato per chiamate HTTP ["+annMethod+"]" +
									", ma è stato invocato da una chiamata HTTP ["+httpMethod+"].";
						}
						
					} else {
						errMsg = "Metodo ["+methodName+"] trovato, ma senza l'appropriata annotation.";
						
					}
				}
			} else {
				errMsg = "Metodo ["+methodName+"] NON trovato.";
			}
		}

		if (errMsg != null) {
			logger.error(errMsg);
			throw new NoSuchMethodException(errMsg);
		}
		
		return retval;
	}
	
	/**
	 * 
	 * @param instance
	 * @param req
	 * @param ctx
	 */
	private void injectParams(Object instance, HttpServletRequest req, ServletContext ctx) {
		if (req != null && ctx != null) {
			Class< ? extends Object> clazz = instance.getClass();
			Field[] fields = clazz.getDeclaredFields();
			Class< ? > type;
			for(Field f : fields) {
				type = f.getType();
				
				String errMsg = "Errore di accesso al campo ["+f.getName()+"]. ";
				
				if (type.getSimpleName().equals(HttpServletRequest.class.getSimpleName())) {
					try {
						FieldUtils.writeField(f, instance, req, true);
					} catch (IllegalAccessException e) {
						logger.error(errMsg, e);
						throw new AutoMagicInvokerException(errMsg, e);
					}
				}
				
				if (type.getSimpleName().equals(ServletContext.class.getSimpleName())) {
					try {
						FieldUtils.writeField(f, instance, ctx, true);
					} catch (IllegalAccessException e) {
						logger.error(errMsg, e);
						throw new AutoMagicInvokerException(errMsg, e);
					}
				}
				
				if (type.getSimpleName().equals(HttpSession.class.getSimpleName())) {
					try {
						FieldUtils.writeField(f, instance, req.getSession(), true);
					} catch (IllegalAccessException e) {
						logger.error(errMsg, e);
						throw new AutoMagicInvokerException(e);
					}
				}
			}
		}
	}

	public Object getInstance(String amActionName) {
		logger.debug("Recupero istanza per [{}]", amActionName);
		String clazz = crawler.resolve(amActionName);
		
		Object instance = null;
		try {
			logger.debug("Creazione istanza di [{}]...", clazz);
			instance = Class.forName(clazz).newInstance();
			
		} catch (InstantiationException e) {
			String msg = "Errore durante la creazione di una nuova istanza di: "+ clazz;
			logger.error(msg, e);
			throw new AutoMagicInvokerException(msg, e);
		} catch (IllegalAccessException e) {
			String msg = "Errore di accesso nella creazione dell'istanza di: "+ clazz;
			logger.error(msg, e);
			throw new AutoMagicInvokerException(msg, e);
		} catch (ClassNotFoundException e) {
			String msg = "Errore classe ["+clazz+"] non trovata. ";
			logger.error(msg, e);
			throw new AutoMagicInvokerException(msg, e);
		}
		logger.trace("Istanza per [{}] creata.", amActionName);
		return instance;
	}
}
