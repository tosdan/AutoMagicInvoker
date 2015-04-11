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
	
	public AutoMagicAction(String webAppRelativeRequestedURI, String invokerRootPath) {
		this.webAppRelativeRequestedURI = webAppRelativeRequestedURI;
		this.invokerRootPath = invokerRootPath;
		init(webAppRelativeRequestedURI, invokerRootPath);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	/**
	 * 
	 * @param webAppRelativeRequestedURI indirizzo chiamato relativo alla webApp
	 * @param rootPath path in cui è mappata la servlet invoker
	 */
	private void init(String webAppRelativeRequestedURI, String invokerRootPath) {
		// URI relativo alla servlet invoker
		String relativeActionURI = webAppRelativeRequestedURI.replace(invokerRootPath, "");				
		relativeActionURI = relativeActionURI.replaceFirst("/", ""); // un percorso relativo che si rispetti non inizia con "/"
		logger.trace("Percorso Azione relativo = [{}]", relativeActionURI);

		// indice della string Classe + Metodo + Render
		int classAndMethodNdx = relativeActionURI.lastIndexOf("/") + 1; // +1 per escludere il carattere '/'
		String classAndMethodId = relativeActionURI.substring(classAndMethodNdx); 
		logger.trace("Sottostringa Classe + Metodo + Render = [{}]", classAndMethodId);
		
		// Indice del punto di separazione tra nome classe e nome metodo
		int methodIdSeparatorStartNdx = classAndMethodId.indexOf(".");
		
		if (methodIdSeparatorStartNdx > -1) { // Se esiste un nome metodo
			// Prozione dell'URI relativo al nome della classe
			String classId = classAndMethodId.substring(0, methodIdSeparatorStartNdx);
			logger.trace("classId = [{}]", classId);
			// Indice dell'ultimo carattere del nome della classe
			int classNameEndNdx = classAndMethodNdx + classId.length();
			
			// l'actionId è il package + il nome della classe
			actionId = relativeActionURI.substring(0, classNameEndNdx);
			String methodIdTmp = classAndMethodId.substring(methodIdSeparatorStartNdx + 1);
			
			int renderStartNdx;
			if ((renderStartNdx = methodIdTmp.indexOf(".")) > -1) {
				renderId = methodIdTmp.substring(renderStartNdx + 1);
				methodId = methodIdTmp.substring(0, renderStartNdx);
			} else {
				methodId = methodIdTmp;
				renderId = "";
			}
			
		} else {
			logger.debug("classId = [{}]", classAndMethodId);
			actionId = relativeActionURI;
			methodId = "";
			renderId = "";
		}

		logger.debug("actionId = [{}]", actionId);
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
}
