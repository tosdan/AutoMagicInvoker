package com.github.tosdan.autominvk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.autominvk.rendering.AutoMagicRender;
import com.github.tosdan.autominvk.rendering.RenderOptions;
import com.github.tosdan.autominvk.rendering.render.DefaultNull;

public class AutoMagicMethodInvoker {

	private static Logger logger = LoggerFactory.getLogger(AutoMagicMethodInvoker.class);
	private IamIvokableClassCrawler crawler;
	private RenderOptions renderOptions;

	public AutoMagicMethodInvoker(IamIvokableClassCrawler crawler) {
		this.crawler = crawler;
		this.renderOptions = new RenderOptions();
		this.renderOptions.setPrettyPrinting(false);
	}
	
	public String getClassPath() {
		return this.crawler.getInvokerRootPath();
	}
	
	private Object invokeMethod(Object instance, String methodId, AutoMagicAction amAction, HttpServletRequest req, HttpServletResponse resp, ServletContext ctx) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String 	httpMethod = amAction.getHttpMethod();
		
		injectParams(instance, req, resp, ctx);
		
		Method method = getMethod(methodId, httpMethod, instance.getClass());
		
		setActionRenderOptionsFromAnnotation(amAction, method);
		forceRenderByAnnotation(amAction, method);
		forceMimeTypeByAnnotation(amAction, method);
	
		Object[] args = null;
		
		if (method.getParameterTypes().length > 0) {
			args = getArgs(req, method);
			logger.trace("Injecting arguments= [{}]", args);
		}

		return method.invoke(instance, args);
	}
	
	/**
	 * 
	 * @param amAction
	 * @param req
	 * @param resp 
	 * @param ctx
	 * @return
	 */
	public Object invoke(AutoMagicAction amAction, HttpServletRequest req, HttpServletResponse resp, ServletContext ctx) {
		Object retval = null;
		String 	methodId = amAction.getMethodId(),
				actionId = amAction.getActionId();
		
		try {

			try {
				Object instance = getInstance(actionId);
				retval = invokeMethod(instance, methodId, amAction, req, resp, ctx);
				
			} catch (AutoMagicInvokerActionNotFoundException e) {
				logger.error(e.getMessage(), e);
				Object instance = getInstance(ActionNotFoundExceptionAmAction.getActionId());
				injectExceptionAndAction(instance, e, amAction);
				retval = invokeMethod(instance, "get", amAction, req, resp, ctx);
			}
		
		} catch (AutoMagicInvokerException e) {
			// catch e rethrow perch� deve essere inoltrata tale e quale altrimenti verrebbe intercettata dal catch di Exception pi� sotto
			throw e;
			
		} catch (com.google.gson.JsonSyntaxException e) {
			String jsonErr = "";
			if (StringUtils.containsIgnoreCase(e.getMessage(), "but was BEGIN_OBJECT at line")) {
				jsonErr = "Il json della request rappresenta un oggetto, ma si sta cercando di popolare un tipo di dato primitivo o una stringa.";
			} else {
				jsonErr = "Gson non � riuscito ad effettuare il parse: json malformato o non compatibile con l'oggetto che si vuole generare.";
			}
			
			throw new AutoMagicInvokerException("Errore: il metodo invocato [" + methodId + "] dell'azione [" + actionId + "] ha generato un errore. " + jsonErr, e);

		} catch (com.google.gson.JsonParseException e) {
			String jsonErr = "";
			if (StringUtils.containsIgnoreCase(e.getMessage(), "The date should be a string value")) {
				jsonErr = "Il formato data ricevuto � diverso da quello impostato in Gson oppure il json ricevuto dalla request rappresenta un oggetto e non una data sotto forma di stringa.";
			} else {
				jsonErr = "Gson non � riuscito ad effettuare il parse: json malformato o non compatibile con l'oggetto che si vuole generare.";
			}
			
			throw new AutoMagicInvokerException("Errore: il metodo invocato [" + methodId + "] dell'azione [" + actionId + "] ha generato un errore. " + jsonErr, e);

		} catch (IllegalAccessException e) {
			throw new AutoMagicInvokerException("Impossibile accedere al metodo [" + methodId + "] dell'azione [" + actionId + "].", e.getCause());
		} catch (InvocationTargetException e) {
			throw new AutoMagicInvokerException("Errore: il metodo invocato [" + methodId + "] dell'azione [" + actionId + "] ha generato un errore.", e.getTargetException());
		} catch (Exception e) {
			// per tutte le altre casisitiche non dipendenti dai componenti del framework
			throw new AutoMagicInvokerException("Errore: il metodo invocato [" + methodId + "] dell'azione [" + actionId + "] ha generato un errore.", e);
		}
		
		return retval;
	}

	private void setActionRenderOptionsFromAnnotation(AutoMagicAction amAction, Method method) {
		IamInvokableAction ann = method.getAnnotation(IamInvokableAction.class);
		String gsonDateFormat = ann.gsonDateFormat();
		String gsonTimeFormat = ann.gsonTimeFormat();
		
		RenderOptions options = new RenderOptions();
		options.setGsonDateFormat(gsonDateFormat);
		options.setGsonTimeFormat(gsonTimeFormat);
		
		amAction.setRenderOptions(options);
	}

	private Object[] getArgs(HttpServletRequest req, Method method) {
		Class<?>[] params = method.getParameterTypes();
		Object[] args = new Object[params.length];
		Class<?> p = null;
		String requestBody = HttpRequestBeanBuilder.parseRequestBody(req);
		for (int i = 0 ; i < params.length ; i++) {
			p = params[i];
			logger.debug("Getting instance of: [{}]", p);
			HttpRequestBeanBuilder beanBuilder = new HttpRequestBeanBuilder();
			args[i] = beanBuilder.buildBeanFromRequest(p, req, requestBody, this.renderOptions);
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
	
	private List<Method> findMethodsById(String methodId, Method[] methods) {
		List<Method> retval = new ArrayList<Method>();
		IamInvokableAction ann;
		for (Method m : methods) {
			ann = m.getAnnotation(IamInvokableAction.class);
			boolean hasAnnotation = (ann != null);
			
			String methodAlias = hasAnnotation && !ann.alias().isEmpty() 
					? ann.alias() 
					: m.getName();
			
			if (methodAlias.equals(methodId)) {
				retval.add(m);
			}
		}
		return retval;
	}	
	
	private List<Method> escludiSenzaAnnotazione(List<Method> listaMetodi) {
		List<Method> retval = new ArrayList<Method>();
		for (Method m : listaMetodi) {
			if (m.getAnnotation(IamInvokableAction.class) != null) {
				retval.add(m);
			}
		}
		return retval;
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
		
		List<Method> listaMetodi = findMethodsById(methodId, methods);
		
		if (listaMetodi.isEmpty()) {
			errMsg = "Metodo ["+methodId+"] NON trovato.";
			throw new AutoMagicInvokerMethodNotFoundException(errMsg);
		}
		
		listaMetodi = escludiSenzaAnnotazione(listaMetodi);
		
		if (listaMetodi.isEmpty()) {
			errMsg = "Non e' stato trovato un metodo ["+methodId+"] con annotation [" + IamInvokableAction.class.getName() + "].";
			throw new AutoMagicInvokerException(errMsg);
			
			
		} else if (listaMetodi.size() > 1) {
			errMsg = "E' stato trovato pi� di un metodo ["+methodId+"] con annotation [" + IamInvokableAction.class.getName() + ". Il massimo supportato dal framework e' di 1 metodo ].";
			throw new AutoMagicInvokerException(errMsg);
			
			
		} else {
			Method m = listaMetodi.get(0);
			ann = m.getAnnotation(IamInvokableAction.class);
			
			String annReqMethod = ann.reqMethod();
			
			this.renderOptions.setGsonDateFormat(ann.gsonDateFormat().isEmpty() ? null : ann.gsonDateFormat());
			this.renderOptions.setGsonTimeFormat(ann.gsonTimeFormat().isEmpty() ? null : ann.gsonTimeFormat());
			boolean isHttpMethodCorrect = annReqMethod.isEmpty() || httpMethod.equalsIgnoreCase(annReqMethod);
			
			if (isHttpMethodCorrect) {
				retval = m;
				errMsg = null;
				
			} else {
				errMsg = "Metodo ["+methodId+"] trovato. Il metodo e' configurato per chiamate HTTP ["+annReqMethod+"]" +
						", ma � stato invocato da una chiamata HTTP ["+httpMethod+"].";
				throw new AutoMagicInvokerException(errMsg);
			}
			
		}
		
		return retval;
	}
	
	/**
	 * 
	 * @param instance
	 * @param e
	 * @param action
	 */
	private void injectExceptionAndAction(Object instance, AutoMagicInvokerActionNotFoundException e, AutoMagicAction action) {
		if (e != null && action != null) {
			Class< ? extends Object> clazz = instance.getClass();
			Field[] fields = clazz.getDeclaredFields();
			Class< ? > type;
			for(Field f : fields) {
				type = f.getType();
				
				String errMsg = "Errore di accesso al campo ["+f.getName()+"]. ";
				
				if (type.isAssignableFrom(AutoMagicInvokerActionNotFoundException.class)) {
					try {
						FieldUtils.writeField(f, instance, e, true);
					} catch (IllegalAccessException e1) {
						throw new AutoMagicInvokerException(errMsg, e1);
					}
				}
				
				if (type.isAssignableFrom(AutoMagicAction.class)) {
					try {
						FieldUtils.writeField(f, instance, action, true);
					} catch (IllegalAccessException e1) {
						throw new AutoMagicInvokerException(errMsg, e1);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param instance
	 * @param req
	 * @param resp 
	 * @param ctx
	 */
	private void injectParams(Object instance, HttpServletRequest req, HttpServletResponse resp, ServletContext ctx) {
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
				
				if (type.isAssignableFrom(HttpServletResponse.class)) {
					try {
						FieldUtils.writeField(f, instance, resp, true);
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

	public Object getInstance(String actionId) throws AutoMagicInvokerActionNotFoundException {
		logger.debug("Recupero istanza per [{}]", actionId);
		String clazz = null;
		Object instance = null;
		
		try {
			clazz = crawler.resolve(actionId);
			
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
		return invoke(amAction, null, null, null);
	}
}
