package com.github.tosdan.autominvk;

import static com.github.tosdan.autominvk.render.Mime.TEXT_HTML;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

import com.github.tosdan.autominvk.render.AutoMagicRender;
import com.github.tosdan.autominvk.render.AutoMagicResponseObject;
import com.github.tosdan.autominvk.render.Default;
import com.github.tosdan.autominvk.render.HttpError;
import com.github.tosdan.autominvk.render.Json;
import com.github.tosdan.autominvk.render.JsonP;

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
		
		AutoMagicAction action = getAction(req);
		
		Object retval = null;
		try {
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
		String renderType = action.getRenderId();
		String mime = action.getMimeType();
		Object response = null;
		AutoMagicRender render = null;
		AutoMagicResponseObject amResponseObject = null;
		
		if (dataToRender instanceof RequestDispatcher) {
			
			((RequestDispatcher) dataToRender).forward(req, resp);
			
			
		} else if (dataToRender instanceof AutoMagicHttpError) {
			
			render = new HttpError();
			amResponseObject = render.getResponseObject(dataToRender, action, req, resp);
			
			
		} else if ("jsonp".equals(renderType)) {
			
			render = new JsonP();
			amResponseObject = render.getResponseObject(dataToRender, action, req, resp);

			
		} else if ("json".equals(renderType)) {

			render = new Json();
			amResponseObject = render.getResponseObject(dataToRender, action, req, resp);
			
			
		} else { // text/html
//			
			render = new Default();
			amResponseObject = render.getResponseObject(dataToRender, action, req, resp);
			
		}
		
		if (amResponseObject != null) {
			mime = amResponseObject.getMimeType();
			response = amResponseObject.getResponseObject();
		}
		
		respond(response, mime, resp);
	}

	/**
	 * 
	 * @param respVal
	 * @param mime
	 * @param resp
	 * @throws IOException
	 */
	private void respond(Object respVal, String mime, HttpServletResponse resp) 
			throws IOException {
		resp.setContentType(mime);
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().print(respVal);
	}
	

	/**
	 * 
	 * @param req
	 * @param httpMethod 
	 * @return
	 */
	private AutoMagicAction getAction(HttpServletRequest req) {
		String ctxPath = ctx.getContextPath();
//		logger.trace("Context Path = [{}]", ctxPath);
		String requestURI = getRequestURI(req);
		logger.trace("RequestedURI = [{}]", requestURI);
		String webAppRelativeRequestedURI = requestURI.replace(ctxPath, "");
		logger.trace("Requested Servlet URI = [{}]", webAppRelativeRequestedURI);
		String invokerServletPath = req.getServletPath();
		logger.trace("Servlet Mapping = [{}]", invokerServletPath);
		
		return new AutoMagicAction(webAppRelativeRequestedURI, invokerServletPath, req.getMethod());
	}


	private String getRequestURI( HttpServletRequest req ) {
		String requestURI = req.getRequestURI();
		try {
			requestURI = URLDecoder.decode(requestURI, "UTF-8");
		} catch ( UnsupportedEncodingException e ) { e.printStackTrace(); }
		return requestURI;
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
		super.destroy();
		this.invoker = null;
		this.ctx = null;
	}

	@Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
	@Override protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { doServ(req, resp); }
}
