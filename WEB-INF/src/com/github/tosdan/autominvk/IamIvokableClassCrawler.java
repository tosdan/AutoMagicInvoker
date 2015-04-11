package com.github.tosdan.autominvk;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniele
 *
 */
public class IamIvokableClassCrawler implements Serializable {
	
	/**
	 * 
	 */
	private static final String ANNOTATION_NAME = IamInvokable.class.getSimpleName();

	private static Logger logger = LoggerFactory.getLogger(IamIvokableClassCrawler.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -944860379950744270L;
	/**
	 * Mappa delle classi recuperate nel classpath specificato.
	 */
	private Map<String, String> actionDatabase;
	private String invokerRootPath;
	
	public IamIvokableClassCrawler(String invokerRootPath) {
		this.invokerRootPath = invokerRootPath;
		this.actionDatabase = new HashMap<String, String>();
		refreshClasses();
	}
	
	/**
	 * Cerca le classi con annotation {@link IamInvokable}
	 * @return
	 */
	public Map<String, String> refreshClasses() {
		Reflections reflections = new Reflections(invokerRootPath);
		
		logger.trace("Ricerca classi annotate con [{}] nel classpath [{}]", ANNOTATION_NAME, invokerRootPath);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(IamInvokable.class);
		logger.trace("Classi trovate: {}", classes.toString());
		
		for (Class<?> clazz : classes) {
			register(clazz, invokerRootPath);
		}
		return actionDatabase;
	}

	/**
	 * 
	 * @param invokerRootPath
	 * @param clazz
	 * @return
	 */
	private String getRelativePackage(String invokerRootPath, Class<?> clazz) {
		String packageName = clazz.getPackage().getName();
		logger.trace("Package name = [{}]", packageName);
		String withoutInvokerRootPath = packageName.replace(invokerRootPath, "");
//		logger.trace("Package name meno rootPath = [{}]", withoutRootPath);
		String relativePackage = withoutInvokerRootPath.replaceFirst("\\.", "");
		logger.trace("Root package = [{}]", invokerRootPath);
		String filler = String.format("%" + (invokerRootPath.length() - 3)+ "s", " ");
		logger.trace("Package relativo = {}[{}]", filler, relativePackage);
		return relativePackage;
	}
	
	/**
	 * 
	 * @param clazz
	 * @param invokerRootPath
	 */
	public void register(Class<?> clazz, String invokerRootPath) {
		IamInvokable ann = clazz.getAnnotation(IamInvokable.class);
		
		String 	classIdAlias = ann.value(),
				classFullName = clazz.getName(),
				classId = getClassId(clazz),
				path = getRelativePackage(invokerRootPath, clazz);
		
		classId = getAnnotatedClassId(classIdAlias, classId);
		path = getActionPath(classIdAlias, path);
		
		String actionId = path + classId;
		
		logger.debug("Register 'actionId' = [{}], resolving to  = [{}]", actionId, classFullName);
		actionDatabase.put(actionId, classFullName);
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
	 * @param classIdAlias
	 * @param path
	 * @return
	 */
	private String getActionPath(String classIdAlias, String path) {
		return classIdAlias.startsWith("/") // Percorso assoluto (ma comunque relativo rispetto al path della servelet principale)
				? "" 
				: path + "/";
	}

	/**
	 * 
	 * @param classIdAlias
	 * @param classId
	 * @return
	 */
	private String getAnnotatedClassId(String classIdAlias, String classId) {
		return classIdAlias != null && ! classIdAlias.isEmpty()
				? classIdAlias.replace("/", "") // Lo slash può essere presente o meno. Nel caso, va tolto.
				: classId;
	}

	/**
	 * Recupera il nome della classe da utilizzare, dal database delle associazioni actionId -> className
	 * @param actionId azione per la quale è necessario recuperare la classe corrispondente
	 * @return il nome completo della classe corrispondente all'actionId passato
	 */
	public String resolve(String actionId) {
		logger.trace("Resolving [{}]", actionId);
		if (actionId == null) {
			throw new AutoMagicInvokerException("actionId NULL.");
		}
		
		String classFullName = actionDatabase.get(actionId);
		logger.trace("Resolved to [{}]", classFullName != null );
		
		if (classFullName == null) {
			String msg = String.format("Nessuna classe registrata con actionId[%s]", actionId);
			logger.error(msg);
			throw new AutoMagicInvokerException(msg);
		}
		
		return classFullName;
	}
}
