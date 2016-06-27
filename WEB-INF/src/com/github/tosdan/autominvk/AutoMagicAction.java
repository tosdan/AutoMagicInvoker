package com.github.tosdan.autominvk;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.autominvk.rendering.AutoMagicRender;
import com.github.tosdan.autominvk.rendering.RenderOptions;
import com.github.tosdan.autominvk.rendering.render.Default;
import com.google.common.base.Throwables;
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
	private Class<? extends AutoMagicRender> render;
	private String httpMethod;
	private String mimeType;
	private RenderOptions renderOptions;
	
	public AutoMagicAction(String webAppRelativeRequestedURI, String invokerRootPath, String httpMethod) {
		this.webAppRelativeRequestedURI = webAppRelativeRequestedURI;
		this.invokerRootPath = invokerRootPath;
		this.httpMethod = httpMethod;
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
			String renderClassSimpleName = classMethodRender.substring(renderStartNdx + 1);
			classMethodRender = classMethodRender.substring(0, renderStartNdx);
			render = getRenderClass(renderClassSimpleName);
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
		logger.debug("Render   = [{}]", render != null ? render.getName() : null);
	}

	@SuppressWarnings( "unchecked" )
	private Class<AutoMagicRender> getRenderClass(String renderClassSimpleName) {
		String 	renderPath = Default.class.getPackage().getName() + ".",
				classFullyQualified = renderPath + renderClassSimpleName;
		
		Class<AutoMagicRender> clazz = null;
		try {
			
			clazz = (Class<AutoMagicRender>) Class.forName(classFullyQualified);
			
		} catch (ClassNotFoundException e) {
			logger.error("Impossibile trovare la classe: [{}]", renderPath + renderClassSimpleName);
			Throwables.propagate(e);
		}
		
		return clazz;
	
	}

	/**
	 * 
	 * @param req
	 * @param httpMethod 
	 * @return
	 */
	public static AutoMagicAction getInstance(HttpServletRequest req, ServletContext ctx) {
		String ctxPath = ctx.getContextPath();
		logger.trace("Context Path = [{}]", ctxPath);
		String requestURI = getRequestURI(req);
		logger.trace("RequestedURI = [{}]", requestURI);
		String webAppRelativeRequestedURI = requestURI.replaceFirst(ctxPath, "");
		logger.trace("Requested Servlet URI = [{}]", webAppRelativeRequestedURI);
		String invokerServletPath = req.getServletPath();
		logger.trace("Servlet Mapping =         [{}]", invokerServletPath);
		
		return new AutoMagicAction(webAppRelativeRequestedURI, invokerServletPath, req.getMethod());
	}
	/**
	 * 
	 * @param req
	 * @return
	 */
	private static String getRequestURI( HttpServletRequest req ) {
		String requestURI = req.getRequestURI();
		try {
			requestURI = URLDecoder.decode(requestURI, "UTF-8");
		} catch ( UnsupportedEncodingException e ) { e.printStackTrace(); }
		return requestURI;
	}
	
	public String getActionId() { return actionId; }
	public void setActionId( String actionId ) { this.actionId = actionId; }
	
	public String getMethodId() { return methodId; }
	public void setMethodId( String methodId ) { this.methodId = methodId; }
	
	public String getHttpMethod() { return httpMethod; }
	public void setHttpMethod( String httpMethod ) { this.httpMethod = httpMethod; }

	public String getMimeType() { return mimeType; }
	public void setMimeType( String mimeType ) { this.mimeType = mimeType; }

	public Class<? extends AutoMagicRender> getRender() { return render; }
	public void setRender( Class<? extends AutoMagicRender> render ) { this.render = render; }
	
	public String getInvokerRootPath() { return invokerRootPath; }
	
	public String getWebAppRelativeRequestedURI() { return webAppRelativeRequestedURI; }
	
	public void setRenderOptions( RenderOptions renderOptions ) { this.renderOptions = renderOptions; }
	public RenderOptions getRenderOptions() { return renderOptions; }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
