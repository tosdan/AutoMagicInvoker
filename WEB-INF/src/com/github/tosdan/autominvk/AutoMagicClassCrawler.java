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
	private Map<String, String> actionToClassesMap;
	private String rootPath;
	
	/**
	 * Cerca le sottoclassi di [superClass] nel classpath specificato
	 * Tali sottoclassi devono però implementare l'annotation {@link IamInvokable}
	 */
	public AutoMagicClassCrawler(String rootPath) {
		this.rootPath = rootPath;
		this.actionToClassesMap = new HashMap<String, String>();
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
		return actionToClassesMap;
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
		
		String 	id = ann.value(),
				classFullName = clazz.getName(),
				classId = getClassId(clazz),
				path = getRelativePackage(rootPath, clazz);
		
		classId = getAnnotatedClassId(id, classId);
		path = getActionPath(id, path);
		
		String actionId = path + classId;
		
		logger.debug("Register 'actionId' = [{}], resolving to  = [{}]", actionId, classFullName);
		actionToClassesMap.put(actionId, classFullName);
	}

	/**
	 * 
	 * @param clazz
	 * @return
	 */
	private String getClassId( Class< ? > clazz ) {
		String classId = clazz.getSimpleName().replaceAll("AmAction", "");
		classId = classId.substring(0,1).toLowerCase() + classId.substring(1);
		return classId;
	}

	/**
	 * 
	 * @param id
	 * @param path
	 * @return
	 */
	private String getActionPath(String id, String path) {
		return id.startsWith("/") // Percorso assoluto (anche se ancora relativo rispetto al path della servelet principale)
				? "" 
				: path + "/";
	}

	/**
	 * 
	 * @param id
	 * @param classId
	 * @return
	 */
	private String getAnnotatedClassId(String id, String classId) {
		return id != null && ! id.isEmpty()
				? id.replace("/", "") // Lo slash può essere presente o meno. Nel caso, va tolto.
				: classId;
	}

	/**
	 * 
	 * @param actionId
	 * @return
	 */
	public String resolve(String actionId) {
		logger.trace("Resolving [{}]", actionId);
		if (actionId == null) {
			throw new AutoMagicInvokerException("actionId NULL.");
		}
		
		String classFullName = actionToClassesMap.get(actionId);
		logger.trace("Resolved to [{}]", classFullName != null );
		
		if (classFullName == null) {
			String msg = String.format("Nessuna classe registrata con actionId[%s]", actionId);
			logger.error(msg);
			throw new AutoMagicInvokerException(msg);
		}
		
		return classFullName;
	}
}
