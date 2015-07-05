package com.github.tosdan.autominvk;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Rappresenta un'azione che è costituita da una classe, un metodo e un render.
 * @author Daniele
 *
 */
public class AutoMagicAction {
	
	private static Logger logger = LoggerFactory.getLogger(AutoMagicAction.class);

	private String actionId;
	private String invokerRootPath;
	private String methodId;
	private String webAppRelativeRequestedURI;
	private String renderId;
	private String httpMethod;
	private String mimeType;
	
	public AutoMagicAction(String webAppRelativeRequestedURI, String invokerRootPath, String httpMethod) {
		this.webAppRelativeRequestedURI = webAppRelativeRequestedURI;
		this.invokerRootPath = invokerRootPath;
		this.httpMethod = httpMethod;
		this.renderId = "";
		init(webAppRelativeRequestedURI, invokerRootPath, httpMethod);
	}
	
	/**
	 * 
	 * @param webAppRelativeRequestedURI indirizzo chiamato relativo alla webApp
	 * @param rootPath path in cui è mappata la servlet invoker
	 */
	private void init(String webAppRelativeRequestedURI, String invokerRootPath, String httpMethod) {
		// URI relativo alla servlet invoker
		String relativeActionURI = webAppRelativeRequestedURI.replace(invokerRootPath, "");				
		relativeActionURI = relativeActionURI.replaceFirst("/", ""); // un percorso relativo che si rispetti non inizia con "/"
		logger.trace("Percorso Azione relativo = [{}]", relativeActionURI);

		// indice della string Classe + Metodo + Render
		int classMethodRenderNdx = relativeActionURI.lastIndexOf("/") + 1; // +1 per escludere il carattere '/'
		String classMethodRender = relativeActionURI.substring(classMethodRenderNdx); 
		logger.trace("Sottostringa Classe + Metodo + Render = [{}]", classMethodRender);
		
		// Indice del punto di separazione tra nome classe e nome metodo
		int methodIdSeparatorStartNdx = classMethodRender.indexOf(".");
		//Indice della tilde di separazione del render
		int renderStartNdx = classMethodRender.indexOf("~");
		
		if (renderStartNdx > -1) {
			renderId = classMethodRender.substring(renderStartNdx + 1);
			classMethodRender = classMethodRender.substring(0, renderStartNdx);
		}
		
		if (methodIdSeparatorStartNdx > -1) {
			methodId = classMethodRender.substring(methodIdSeparatorStartNdx + 1);
			classMethodRender = classMethodRender.substring(0, methodIdSeparatorStartNdx);
			
		} else {
			// Default methodId = httpMethod from request
			logger.debug("Setting [methodId] to default [{}] from HTTP request.", httpMethod.toLowerCase());
			methodId = httpMethod.toLowerCase();
		}

		// Indice dell'ultimo carattere del nome della classe
		int classNameEndNdx = classMethodRenderNdx + classMethodRender.length();

		// l'actionId è il package + il nome della classe
		actionId = relativeActionURI.substring(0, classNameEndNdx);

		logger.debug("actionId = [{}]", actionId);
		logger.debug("classId = [{}]", classMethodRender);
		logger.debug("methodId = [{}]", methodId);
		logger.debug("Render   = [{}]", renderId);
	}

	public String getActionId() {
		return actionId;
	}
	public void setActionId( String actionId ) {
		this.actionId = actionId;
	}
	
	public String getMethodId() {
		return methodId;
	}
	public void setMethodId( String methodId ) {
		this.methodId = methodId;
	}
	
	public String getRenderId() {
		return renderId;
	}
	public void setRenderId( String renderId ) {
		this.renderId = renderId;
	}
	
	public String getInvokerRootPath() {
		return invokerRootPath;
	}
	
	public String getWebAppRelativeRequestedURI() {
		return webAppRelativeRequestedURI;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod( String httpMethod ) {
		this.httpMethod = httpMethod;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType( String mimeType ) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
