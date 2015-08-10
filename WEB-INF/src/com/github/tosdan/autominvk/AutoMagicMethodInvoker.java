package com.github.tosdan.autominvk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.autominvk.rendering.AutoMagicRender;
import com.github.tosdan.autominvk.rendering.render.DefaultNull;
import com.github.tosdan.utils.varie.HttpReuqestUtils;

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
			
			if (method.getParameterTypes().length > 0) {
				args = getArgs(req, method);
				logger.debug("Injecting arguments= [{}]", args);
			}
			
			retval = method.invoke(instance, args);
		
		} catch ( IllegalAccessException e ) {
			throw new AutoMagicInvokerException("Impossibile accedere al metodo [" + methodId + "] dell'azione [" + actionId + "].", e.getCause());
		} catch ( InvocationTargetException e ) {
			throw new AutoMagicInvokerException("Errore: il metodo invocato [" + methodId + "] dell'azione [" + actionId + "] ha generato un errore.", e.getTargetException());
		}
		
		
		return retval;
	}

	private Object[] getArgs(HttpServletRequest req, Method method) {
		Class<?>[] params = method.getParameterTypes();
		Object[] args = new Object[params.length];
		Class<?> p;
		for (int i = 0 ; i < params.length ; i++) {
			p = params[i];
			logger.debug("Getting instance of: [{}]", p);
			args[i] = HttpReuqestUtils.buildBeanFromRequest(req, p);
		}
		return args;
	}
	
	private void forceRenderByAnnotation( AutoMagicAction amAction, Method method ) {
		IamInvokableAction ann = method.getAnnotation(IamInvokableAction.class);
		
		Class<? extends AutoMagicRender> clazz = ann.render();
		boolean isDefault = clazz.equals(DefaultNull.class);
		
		if (!isDefault || amAction.getRender() == null) { // defaultNull solo quando via request non hanno specificato il ~render
			amAction.setRender(clazz);
			logger.debug("Render forzato tramite annotation=[{}]", clazz.getName());
		}
	}
	
	private void forceMimeTypeByAnnotation( AutoMagicAction amAction, Method method ) {
		IamInvokableAction ann = method.getAnnotation(IamInvokableAction.class);
		String mime = ann.mime();
		if (mime != null && !mime.isEmpty()) {
			logger.debug("Mime forzato tramite annotation=[{}]", mime);
			amAction.setMimeType(mime);
		}
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
					errMsg = "Non e' stato trovato un metodo ["+methodId+"] con annotation [" + IamInvokableAction.class.getName() + "].";
					
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
				
				if (type.isAssignableFrom(HttpServletRequest.class)) {
					try {
						FieldUtils.writeField(f, instance, req, true);
					} catch (IllegalAccessException e) {
						throw new AutoMagicInvokerException(errMsg, e);
					}
				}
				
				if (type.isAssignableFrom(ServletContext.class)) {
					try {
						FieldUtils.writeField(f, instance, ctx, true);
					} catch (IllegalAccessException e) {
						throw new AutoMagicInvokerException(errMsg, e);
					}
				}
				
				if (type.isAssignableFrom(HttpSession.class)) {
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
			throw new AutoMagicInvokerException("Errore durante la creazione di una nuova istanza di: "+ clazz, e.getCause());
		} catch (IllegalAccessException e) {
			throw new AutoMagicInvokerException("Errore di accesso nella creazione dell'istanza di: "+ clazz, e.getCause());
		} catch (ClassNotFoundException e) {
			throw new AutoMagicInvokerException("Errore classe ["+clazz+"] non trovata.", e.getCause());
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
