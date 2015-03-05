package com.github.tosdan.autominvk;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Rappresenta una azione che è costituita da una classe, un metodo e un render.
 * @author Daniele
 *
 */
public class AutoMagicAction {
	
	private static Logger logger = LoggerFactory.getLogger(AutoMagicAction.class);

	private String actionId;
	private String rootPath;
	private String methodId;
	private String fullPathActionName;
	private String renderId;
	
	public AutoMagicAction(String fullPathActionName, String rootPath) {
		this.fullPathActionName = fullPathActionName;
		this.rootPath = rootPath;
		init(fullPathActionName, rootPath);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	private void init( String fullPathActionName, String rootPath ) {
		String withoutRootPath = fullPathActionName.replace(rootPath, "");
		withoutRootPath = withoutRootPath.replaceFirst("/", "");
		logger.trace("Percorso Azione meno Root Path e senza primo '/' = [{}]", withoutRootPath);

		int classAndMethodNdx = withoutRootPath.lastIndexOf("/") + 1; // +1 per escludere il carattere '/'
		String classAndMethodId = withoutRootPath.substring(classAndMethodNdx); 
		logger.trace("Sottostringa Classe + Metodo = [{}]", classAndMethodId);
		
		// Indice del punto di separazione tra nome classe e nome metodo
		int methodIdSeparatorNdx = classAndMethodId.indexOf(".");
		
		if (methodIdSeparatorNdx > -1) {
			// Prozione dell'URI relativo al nome della classe
			String classId = classAndMethodId.substring(0, methodIdSeparatorNdx);
			logger.trace("classId = [{}]", classId);
			// Indice dell'ultimo carattere del nome della classe
			int classNameNdxEnd = withoutRootPath.indexOf(classId) + classId.length();
			
			actionId = withoutRootPath.substring(0, classNameNdxEnd);
			methodId = classAndMethodId.substring(methodIdSeparatorNdx + 1);
			
			int renderNdx;
			if ((renderNdx = methodId.indexOf(".")) > -1) {
				renderId = methodId.substring(renderNdx + 1);
				methodId = methodId.substring(0, renderNdx);
			} else {
				renderId = "";
			}
			
		} else {
			logger.debug("classId = [{}]", classAndMethodId);
			actionId = withoutRootPath;
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
	
	public String getRootPath() {
		return rootPath;
	}
	
	public String getFullPathActionName() {
		return fullPathActionName;
	}
}
