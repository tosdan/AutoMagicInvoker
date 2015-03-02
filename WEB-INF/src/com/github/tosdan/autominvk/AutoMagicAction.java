package com.github.tosdan.autominvk;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoMagicAction {
	
	private static Logger logger = LoggerFactory.getLogger(AutoMagicAction.class);

	private String azioneName;
	private String rootPath;
	private String resolvedClass;
	private String methodName;
	private String fullAzioneName;
	
	public AutoMagicAction(String fullAzioneName, String rootPath) {
		this.fullAzioneName = fullAzioneName;
		this.rootPath = rootPath;
		extractData(fullAzioneName, rootPath);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	private void extractData( String fullAzioneName, String rootPath ) {
		String withoutRootPath = fullAzioneName.replace(rootPath, "");
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
			
			azioneName = withoutRootPath.substring(0, classNameIdxEnd);
			methodName = classAndMethod.substring(methodNameSeparatorIdx + 1);
			
		} else {
			azioneName = withoutRootPath;
			methodName = "";
		}

		logger.trace("methodName = [{}]", methodName);
		logger.trace("azioneName = [{}]", azioneName);
	}

	public String getActionName() {
		return azioneName;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getResolvedClass() {
		return resolvedClass;
	}
	
	public String getRootPath() {
		return rootPath;
	}
	
	public String getFullAzioneName() {
		return fullAzioneName;
	}
}
