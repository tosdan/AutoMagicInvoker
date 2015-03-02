package com.github.tosdan.autominvk;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoMagicClassCrawler implements Serializable {
	
	/**
	 * 
	 */
	private static final String ANNOTATION_NAME = AutoMagicInvokable.class.getSimpleName();

	private static Logger logger = LoggerFactory.getLogger(AutoMagicClassCrawler.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -944860379950744270L;
	/**
	 * Mappa delle sotto classi recuperate nel classpath specificato.
	 */
	private Map<String, String> annotatedClassesMap;
	private String rootPath;
	
	/**
	 * Cerca le sottoclassi di [superClass] nel classpath specificato
	 * Tali sottoclassi devono però implementare l'annotation {@link AutoMagicInvokable}
	 */
	public AutoMagicClassCrawler(String rootPath) {
		this.rootPath = rootPath;
		this.annotatedClassesMap = new HashMap<String, String>();
		refreshClasses();
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> refreshClasses() {
		Reflections reflections = new Reflections(rootPath);
		
		logger.trace("Ricerca classi annotate con [{}] nel classpath [{}]", ANNOTATION_NAME, rootPath);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(AutoMagicInvokable.class);
		logger.trace("Classi trovate: {}", classes.toString());
		
		for (Class<?> clazz : classes) {
			register(clazz, rootPath);
		}
		return annotatedClassesMap;
	}

	/**
	 * 
	 * @param rootPath
	 * @param clazz
	 * @return
	 */
	private String getRelativePackage(String rootPath, Class<?> clazz) {
		String packageName = clazz.getPackage().getName();
		logger.trace("Package name = [{}]", packageName);
		String withoutRootPath = packageName.replace(rootPath, "");
//		logger.trace("Package name meno rootPath = [{}]", withoutRootPath);
		String relativePackage = withoutRootPath.replaceFirst("\\.", "");
		logger.trace("Root package = [{}]", rootPath);
		String filler = String.format("%" + (rootPath.length() - 3)+ "s", " ");
		logger.trace("Package relativo = {}[{}]", filler, relativePackage);
		return relativePackage;
	}
	
	/**
	 * 
	 * @param clazz
	 * @param rootPath
	 */
	public void register(Class<?> clazz, String rootPath) {
		String 	className = clazz.getSimpleName(),
				classFullName = clazz.getName(),
				actionName = getRelativePackage(rootPath, clazz) +"/"+ className;
		
		logger.debug("Register 'actionName' = [{}], resolving to  = [{}]", actionName, classFullName);
		annotatedClassesMap.put(actionName, classFullName);
	}

	/**
	 * 
	 * @param actionName
	 * @return
	 */
	public String resolve(String actionName) {
		logger.trace("Resolving [{}]", actionName);
		if (actionName == null) {
			throw new AutoMagicInvokerException("actionName NULL.");
		}
		
		String classFullName = annotatedClassesMap.get(actionName);
		logger.trace("Resolved to [{}]", classFullName != null );
		
		if (classFullName == null) {
			String msg = String.format("Nessuna classe registrata con actionName[%s]", actionName);
			logger.error(msg);
			throw new AutoMagicInvokerException(msg);
		}
		
		return classFullName;
	}
}
