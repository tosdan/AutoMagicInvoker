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
	private IamIvokableClassCrawler crawler;

	public AutoMagicMethodInvoker(IamIvokableClassCrawler crawler) {
		this.crawler = crawler;
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
		String 	methodId = amAction.getMethodId(),
				actionId = amAction.getActionId(),
				httpMethod = amAction.getHttpMethod();
		
		Object instance = getInstance(actionId);
		injectParams(instance, req, ctx);
		
		Method method = getMethod(methodId, httpMethod, instance.getClass());
		
		forceRenderByAnnotation(amAction, method);
		forceMimeTypeByAnnotation(amAction, method);
		
		try {

			Object[] args = null;
			retval = method.invoke(instance, args);
		
		} catch ( IllegalAccessException e ) {
			throw new AutoMagicInvokerException("Impossibile accedere al metodo [" + methodId + "] dell'azione [" + actionId + "].", e);
		} catch ( InvocationTargetException e ) {
			throw new AutoMagicInvokerException("Errore in esecuzione del metodo [" + methodId + "] dell'azione [" + actionId + "].", e);
		}
		
		retval = postProcess(retval, method, actionId);
		
		
		return retval;
	}

	private void forceRenderByAnnotation( AutoMagicAction amAction, Method method ) {
		IamInvokableAction ann = method.getAnnotation(IamInvokableAction.class);
		String render = ann.render();
		if (render != null && !render.isEmpty()) {
			amAction.setRenderId(render);
		}
	}
	
	private void forceMimeTypeByAnnotation( AutoMagicAction amAction, Method method ) {
		IamInvokableAction ann = method.getAnnotation(IamInvokableAction.class);
		String mime = ann.mime();
		if (mime != null && !mime.isEmpty()) {
			amAction.setMimeType(mime);
		}
	}

	private Object postProcess(Object input, Method method, String actionId) {
		Object retval = input;
		IamInvokableAction ann = method.getAnnotation(IamInvokableAction.class);
		String[] mapify = ann.mapify();
		logger.debug("Campi da utilizzare per Mappificazione = {}", Arrays.asList(mapify));
		
		if (hasValue(mapify)) {
			try {
				
				retval = MapMaker.getMap(retval, mapify);
				
				
			} catch (IllegalArgumentException e) {
				throw new AutoMagicInvokerException("Errore di accesso ai campi in fase di Post Process per il metodo ["+method.getName()+"] dell'azione ["+actionId+"].", e.getCause());
				
			} catch (IllegalAccessException e) {
				throw new AutoMagicInvokerException("Errore in fase di Post Process per il metodo ["+method.getName()+"] dell'azione ["+actionId+"].", e.getCause());
			}
		}
		return retval;
	}

	private boolean hasValue( String[] mapify ) {
		return mapify.length > 0 && !mapify[0].isEmpty();
	}

	/**
	 * 
	 * @param methodId
	 * @param httpMethod
	 * @param clazz
	 * @return
	 */
	private Method getMethod(String methodId, String httpMethod, Class<?> clazz) {
		Method retval = null;
		IamInvokableAction ann;
		String errMsg = null;
		
		Method[] methods = clazz.getDeclaredMethods();
		
		for(Method m : methods) {
			ann = m.getAnnotation(IamInvokableAction.class);
			boolean hasAnnotation = (ann != null);
			
			String methodAlias = hasAnnotation && !ann.alias().isEmpty() 
					? ann.alias() 
					: m.getName();
			
			boolean mwthodFound = methodAlias.equals(methodId);
			
			if (mwthodFound) {
				int parametersLentgh = m.getParameterTypes().length;
				
				if (parametersLentgh > 0) {
					errMsg = "Metodo ["+methodId+"] trovato. I metodi non possono avere parametri."; // non al momento almeno
					
					
				} else {
					
					if (hasAnnotation) {
						
						
						String annMethod = ann.reqMethod();
						boolean isHttpMethodCorrect = annMethod.isEmpty() || httpMethod.equalsIgnoreCase(annMethod);
						
						if (isHttpMethodCorrect) {
							retval = m;
							errMsg = null;
							break;
							
							
							
						} else {
							errMsg = "Metodo ["+methodId+"] trovato. Il metodo e' configurato per chiamate HTTP ["+annMethod+"]" +
									", ma è stato invocato da una chiamata HTTP ["+httpMethod+"].";
						}
						
					} else {
						errMsg = "Non e' stato trovato un metodo ["+methodId+"] con annotation [" + IamInvokableAction.class.getSimpleName() + "].";
						
					}
				}
			} else {
				errMsg = "Metodo ["+methodId+"] NON trovato.";
			}
		}

		if (errMsg != null) {
			throw new AutoMagicInvokerException(errMsg);
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
				String typeName = type.getSimpleName();
				
				if (typeName.equals(HttpServletRequest.class.getSimpleName())) {
					try {
						FieldUtils.writeField(f, instance, req, true);
					} catch (IllegalAccessException e) {
						throw new AutoMagicInvokerException(errMsg, e);
					}
				}
				
				if (typeName.equals(ServletContext.class.getSimpleName())) {
					try {
						FieldUtils.writeField(f, instance, ctx, true);
					} catch (IllegalAccessException e) {
						throw new AutoMagicInvokerException(errMsg, e);
					}
				}
				
				if (typeName.equals(HttpSession.class.getSimpleName())) {
					try {
						FieldUtils.writeField(f, instance, req.getSession(), true);
					} catch (IllegalAccessException e) {
						throw new AutoMagicInvokerException(errMsg, e);
					}
				}
			}
		}
	}

	public Object getInstance(String actionId) {
		logger.debug("Recupero istanza per [{}]", actionId);
		String clazz = crawler.resolve(actionId);
		
		Object instance = null;
		
		try {
			
			logger.debug("Creazione istanza di [{}]...", clazz);
			instance = Class.forName(clazz).newInstance();
			
			
		} catch (InstantiationException e) {
			throw new AutoMagicInvokerException("Errore durante la creazione di una nuova istanza di: "+ clazz, e);
		} catch (IllegalAccessException e) {
			throw new AutoMagicInvokerException("Errore di accesso nella creazione dell'istanza di: "+ clazz, e);
		} catch (ClassNotFoundException e) {
			throw new AutoMagicInvokerException("Errore classe ["+clazz+"] non trovata.", e);
		}
		
		
		logger.trace("Istanza per [{}] creata.", actionId);
		return instance;
	}
	


	/**
	 * @deprecated Solo per test
	 * @param amAction
	 * @return
	 */
	public Object invoke(AutoMagicAction amAction) {
		return invoke(amAction, null, null);
	}
}
