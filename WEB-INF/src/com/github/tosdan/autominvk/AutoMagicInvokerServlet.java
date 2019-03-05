package com.github.tosdan.autominvk;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.autominvk.rendering.AutoMagicRender;
import com.github.tosdan.autominvk.rendering.AutoMagicResponseObject;
import com.github.tosdan.autominvk.rendering.render.Default;
import com.github.tosdan.autominvk.rendering.render.DefaultNull;
import com.github.tosdan.autominvk.rendering.render.HttpError;
import com.github.tosdan.autominvk.rendering.render.Json2;
import com.google.common.base.ThrowablesRevive;

public class AutoMagicInvokerServlet extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(AutoMagicInvokerServlet.class);
	private ServletContext ctx;
	private AutoMagicMethodInvoker invoker;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5058109197912737051L;
	private String classpath;
	
	/**
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doServ(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String httpMethod = req.getMethod();
		logger.trace("Metodo HTTP = [{}]", httpMethod);
		
		AutoMagicAction action = null;
		
		Object retval = null;
		try {
			action = AutoMagicAction.getInstance(req, ctx);
			
			logger.trace("{}", action);
			
			retval = invoker.invoke(action, req, resp, ctx);

			sendResponse(retval, action, req, resp);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			sendResponse(e, action, req, resp);
		}
	}


	/**
	 * 
	 * @param dataToRender
	 * @param action
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void sendResponse(Object dataToRender, AutoMagicAction action, HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		Class<? extends AutoMagicRender> renderClass = action.getRender();
		Object response = null;
		String charset = "UTF-8";
		HttpRequestHeadersUtil headersUtil = new HttpRequestHeadersUtil(req);
		boolean xRequestedWith = headersUtil.containsXRequestedWith();
		boolean acceptJson = headersUtil.isAcceptApplicationJson();
		
		AutoMagicRender render = null;
		AutoMagicResponseObject amRespObj = null;
		
		logger.debug("Dati da renderizzare: [{}]", dataToRender);
		
		if (dataToRender instanceof RequestDispatcher) {
			logger.debug("Forwarding...");
			((RequestDispatcher) dataToRender).forward(req, resp);
			
			
		} else {
			logger.debug("Rendering phase...");
			if (dataToRender instanceof AutoMagicHttpError) {
				logger.debug("dataToRender instanceof AutoMagicHttpError");
				render = new HttpError();

				
			} else if (DefaultNull.class.equals(renderClass) && (xRequestedWith || acceptJson)) {
				// -> quando non è stato specificato un render
				logger.debug("renderClass instanceof DefaultNull");
				logger.debug("Detected X-Requested-With={}, Accept:application/json={} => forcing Json render...", xRequestedWith, acceptJson);
				// render di default per le richieste Ajax
				render = new Json2();
				
				
			} else if (renderClass != null) {
				// -> quando è stato scelto un render specifico
				logger.debug("renderClass != NULL");
				render = getRenderInstance(renderClass);
				
			
			} else {
				logger.debug("renderClass IS NULL");
				// -> per esempio non è stata trovata corrispondenza per il nome di controller/action richiesto/a
				if (xRequestedWith || acceptJson) {
					logger.debug("Detected [X-Requested-With = {}], [Accept:application/json = {}] => forcing Json render...",
							xRequestedWith, acceptJson);
					render = new Json2();
				} else {
					render = new Default();
				}				
			}
			
			logger.debug("Render Instance: [{}]", render.getClass().getName());
			amRespObj = render.getResponseObject(dataToRender, action, req, resp);

			String mime = action.getMimeType();
			
			if (amRespObj != null) {
				mime = amRespObj.getMimeType();
				response = amRespObj.getResponseObject();
				charset = StringUtils.defaultIfBlank(amRespObj.getCharset(), charset);

			}
			respond(response, mime, charset, resp);
		}
		
	}

	/**
	 * 
	 * @param render
	 * @return
	 */
	private AutoMagicRender getRenderInstance(Class<? extends AutoMagicRender> render) {
		
		AutoMagicRender instance = null;
		try {
			
			instance = render.newInstance();
			
		} catch ( InstantiationException e ) {
			logger.error("Impossibile creare un'istanza della classe: [{}]", render.getName());
		} catch ( IllegalAccessException e ) {
			logger.error("Impossibile inizializzare l'istanza della classe: [{}]", render.getName());
			ThrowablesRevive.propagate(e);
		}
		
		return instance;
	}


	/**
	 * 
	 * @param respVal
	 * @param mime
	 * @param charset 
	 * @param resp
	 * @throws IOException
	 */
	private void respond(Object respVal, String mime, String charset, HttpServletResponse resp) 
			throws IOException {
		if (!resp.isCommitted()) {
			resp.setContentType(mime);
			resp.setCharacterEncoding(charset);
			resp.getWriter()
				.print(respVal);
			
		} else {
			logger.trace("Sulla [response] è già stato fatto il commit! Perciò non è stato possibile invaire i seguenti dati al client:");
			logger.trace(String.valueOf(respVal));
		}
	}
	

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ctx = config.getServletContext();
		classpath = config.getInitParameter("CLASS_PATH");
		logger.debug("ClassPath in cui verranno cercate le classi che implementano IamInvokable = [{}]", classpath);
		// Raccoglie tutte le classi da CLASS_PATH che siano annotate con IamInvokable
		IamIvokableClassCrawler crawler = new IamIvokableClassCrawler(classpath);
		// Configura un nuovo invoker che attingerà alle classi raccolte dal crawler
		this.invoker = new AutoMagicMethodInvoker(crawler);
	}
	
	@Override
	public void destroy() {	
		this.invoker = null;
		this.ctx = null;
		this.classpath = null;
		super.destroy();
	}

	@Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
}
