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
import com.github.tosdan.autominvk.rendering.render.HttpError;
import com.google.common.base.Throwables;

public class AutoMagicInvokerServlet extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(AutoMagicInvokerServlet.class);
	private ServletContext ctx;
	private AutoMagicMethodInvoker invoker;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5058109197912737051L;
	
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
			
			retval = invoker.invoke(action, req, ctx);

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
		
		AutoMagicRender render = null;
		AutoMagicResponseObject amRespObj = null;
		
		logger.debug("Dati da renderizzare: [{}]", dataToRender);
		
		if (dataToRender instanceof RequestDispatcher) {
			((RequestDispatcher) dataToRender).forward(req, resp);
			
			
		} else if (dataToRender instanceof AutoMagicHttpError) {
			render = new HttpError();

			
		} else if (renderClass != null) {
			render = getRenderInstance(renderClass);
			
			
		} else {
			render = new Default();
			
		}
		
		 // Per esempio, un caso lecito in cui renderInstance è null, capita quando venga restituito un dispatcher.
		if (render != null) {
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
			Throwables.propagate(e);
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
		resp.setContentType(mime);
		resp.setCharacterEncoding(charset);
		resp.getWriter()
			.print(respVal);
	}
	

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ctx = config.getServletContext();
		String classpath = config.getInitParameter("CLASS_PATH");
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
