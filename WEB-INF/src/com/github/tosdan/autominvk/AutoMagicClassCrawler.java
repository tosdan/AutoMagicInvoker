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
	private static final String ANNOTATION_NAME = IamInvokable.class.getSimpleName();

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
	 * Tali sottoclassi devono però implementare l'annotation {@link IamInvokable}
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
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(IamInvokable.class);
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
		IamInvokable ann = clazz.getAnnotation(IamInvokable.class);
		
		String 	alias = ann.value(),
				classFullName = clazz.getName(),
				className = getClassName(clazz),
				path = getRelativePackage(rootPath, clazz);
		
		className = getClassAlias(alias, className);
		path = getActionPath(alias, path);
		
		String actionName = path + className;
		
		logger.debug("Register 'actionName' = [{}], resolving to  = [{}]", actionName, classFullName);
		annotatedClassesMap.put(actionName, classFullName);
	}

	/**
	 * 
	 * @param clazz
	 * @return
	 */
	private String getClassName( Class< ? > clazz ) {
		String className = clazz.getSimpleName().replaceAll("AmAction", "");
		className = className.substring(0,1).toLowerCase() + className.substring(1);
		return className;
	}

	/**
	 * 
	 * @param alias
	 * @param path
	 * @return
	 */
	private String getActionPath(String alias, String path) {
		return alias.startsWith("/") // Percorso assoluto (anche se ancora relativo rispetto al path della servelet principale)
				? "" 
				: path + "/";
	}

	/**
	 * 
	 * @param alias
	 * @param className
	 * @return
	 */
	private String getClassAlias(String alias, String className) {
		return alias != null && ! alias.isEmpty()
				? alias.replace("/", "") // Lo slash può essere presente o meno. Nel caso, va tolto.
				: className;
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
