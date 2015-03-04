package com.github.tosdan.autominvk;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoMagicAction {
	
	private static Logger logger = LoggerFactory.getLogger(AutoMagicAction.class);

	private String actionName;
	private String rootPath;
	private String methodName;
	private String fullPathActionName;
	private String render;
	
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

		int classAndMethodIdx = withoutRootPath.lastIndexOf("/") + 1; // +1 per escludere il carattere '/'
		String classAndMethod = withoutRootPath.substring(classAndMethodIdx); 
		logger.trace("Sottostringa Classe + Metodo = [{}]", classAndMethod);
		
		// Indice del punto di separazione tra nome classe e nome metodo
		int methodNameSeparatorIdx = classAndMethod.indexOf(".");
		
		if (methodNameSeparatorIdx > -1) {
			// Prozione dell'URI relativo al nome della classe
			String classeName = classAndMethod.substring(0, methodNameSeparatorIdx);
			logger.trace("classeName = [{}]", classeName);
			// Indice dell'ultimo carattere del nome della classe
			int classNameIdxEnd = withoutRootPath.indexOf(classeName) + classeName.length();
			
			actionName = withoutRootPath.substring(0, classNameIdxEnd);
			methodName = classAndMethod.substring(methodNameSeparatorIdx + 1);
			
			int renderIdx;
			if ((renderIdx = methodName.indexOf(".")) > -1) {
				render = methodName.substring(renderIdx + 1);
				methodName = methodName.substring(0, renderIdx);
			} else {
				render = "";
			}
			
		} else {
			actionName = withoutRootPath;
			methodName = "";
			render = "";
		}

		logger.trace("azioneName = [{}]", actionName);
		logger.trace("methodName = [{}]", methodName);
		logger.trace("Render = [{}]", render);
	}

	public String getActionName() {
		return actionName;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getRootPath() {
		return rootPath;
	}
	
	public String getFullPathActionName() {
		return fullPathActionName;
	}
	
	public String getRender() {
		return render;
	}
}
