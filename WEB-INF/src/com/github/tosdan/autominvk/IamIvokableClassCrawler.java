package com.github.tosdan.autominvk;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

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
		register(ActionNotFoundExceptionAmAction.class, invokerRootPath); // Action di default usata ogni volta che viene richiesta una action inesistente (creare una classe omonima per nel CLASS_PATH specificato nel web.xml per sovrascrivere questa di default)
		refreshClasses();
	}
	
	public String getInvokerRootPath() {
		return invokerRootPath;
	}
	
	/**
	 * Cerca le classi con annotation {@link IamInvokable}
	 * @return
	 */
	public Map<String, String> refreshClasses() {

		ClassGraph scanner = new ClassGraph().enableAllInfo().whitelistPackages(invokerRootPath);
		
		logger.trace("Ricerca classi annotate con [{}] nel classpath [{}]", ANNOTATION_NAME, invokerRootPath);
		ScanResult result = scanner.scan();
		
		List<ClassInfo> routeClassInfoList  = result.getClassesWithAnnotation(IamInvokable.class.getName());
		
		logger.trace("Classi trovate: {}", routeClassInfoList.toString());
		
		for (ClassInfo classInfo : routeClassInfoList) {
			Class< ? > clazz = classInfo.loadClass();
			
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
		String withoutInvokerRootPath = packageName.replaceFirst(invokerRootPath, "");
//		logger.trace("Package name meno rootPath = [{}]", withoutRootPath);
		String relativePackage = StringUtils.removeStart(withoutInvokerRootPath, ".");
//		String relativePackage = withoutInvokerRootPath.replaceFirst("\\.", ""); // l'idea era di rimuovere il punto residuo dopo la replace. Quindi StringUtils.removeStart() dovrebbe essere più appropriato
		relativePackage = relativePackage.replace(".", "/");
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
	 * Recupera il nome della classe-azione in camelCase (Di norma i nomi delle classi normalmente sono in PascalCase)
	 * @param clazz Classe dell'azione.
	 * @return
	 */
	private String getClassId( Class< ? > clazz ) {
		String classId = clazz.getSimpleName().replaceAll("AmAction", "");
		classId = classId.substring(0,1).toLowerCase() + classId.substring(1);
		return classId;
	}

	/**
	 * Determina il percorso dell'azione.
	 * @param classIdAlias stringa assegnata al valore dell'annotazione {@link IamInvokable}
	 * @param path Percorso dell'azione. Corrisponde al package completo meno il package radice.
	 * Il package radice è quello da cui parte la ricerca delle classi azione.
	 * @return
	 */
	private String getActionPath(String classIdAlias, String path) {
		return classIdAlias.startsWith("/") // Percorso assoluto (ma comunque relativo rispetto al path della servlet principale)
				? "" 
				: path.isEmpty() 
						? "" 
						: path + "/";
	}

	/**
	 * Recupera il nome dell'azione. Corrisponde al nome della classe in camelCase oppure al valore assegnato
	 * all'annotazione {@link IamInvokable} <strong>Esempio:</strong> <em>IamInvokable( "valoreAssegnato" )</em>.
	 * @param classIdAlias stringa assegnata al valore dell'annotazione {@link IamInvokable}
	 * @param classId Nome della classe senza package.
	 * @return
	 */
	private String getAnnotatedClassId(String classIdAlias, String classId) {
		return classIdAlias != null && ! classIdAlias.isEmpty()
				? classIdAlias.startsWith("/") 
						// Lo iniziale slash può essere presente o meno. Se presente, va tolto (serve  
						// per determinare il path (in getActionPath()), assoluto o relativo, non il nome dell'azione).
						? classIdAlias.substring(1) 
						: classIdAlias
				: classId;
	}

	/**
	 * Recupera il nome della classe da utilizzare, dal database delle associazioni actionId -> className
	 * @param actionId azione per la quale è necessario recuperare la classe corrispondente
	 * @return il nome completo della classe corrispondente all'actionId passato
	 */
	public String resolve(String actionId) throws AutoMagicInvokerActionNotFoundException {
		logger.trace("Resolving [{}]", actionId);
		if (actionId == null) {
			throw new AutoMagicInvokerException("actionId NULL.");
		}
		
		String classFullName = actionDatabase.get(actionId);
		logger.trace("Resolved to [{}]", classFullName != null );
		
		if (classFullName == null) {
			String msg = String.format("Nessuna classe registrata con actionId[%s]", actionId);
			logger.error(msg);
			throw new AutoMagicInvokerActionNotFoundException(msg);
		}
		
		return classFullName;
	}
}
