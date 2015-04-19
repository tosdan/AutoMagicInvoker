package com.github.tosdan.autominvk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AutoMagicInvokerServlet extends HttpServlet {
	private static final String TEXT_HTML = "text/html";
	private static final String TEXT_PLAIN = "text/plain";
	private static final String TEXT_JAVASCRIPT = "text/javascript";
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
			logger.error("{}", e);
			sendResponse(e, action, req, resp);
		}
	}


	/**
	 * 
	 * @param result
	 * @param action
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void sendResponse(Object result, AutoMagicAction action, HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String render = action.getRenderId();
		
		if (result instanceof RequestDispatcher) {
			((RequestDispatcher) result).forward(req, resp);
			
			
		} else if ("jsonp".equals(render)) {
			String callback = req.getParameter("callback");
			if (result instanceof Exception)  {
				result = getExcptionMap((Exception) result);
				
			} else if (callback == null) {
				result = getExcptionMap(new IllegalArgumentException("Parametro callback mancante nella request."));
				
			}
			String jsonP = callback +"(" + getGson().toJson(result) + ")";
			
			respond(jsonP, TEXT_JAVASCRIPT, resp);
			
			
			
		} else if ("json".equals(render)) {
			if (result instanceof Exception)  {
				result = getExcptionMap((Exception) result);
			}
			String json = getGson().toJson(result);
			
			respond(json, TEXT_PLAIN, resp);
			
			
		} else if ("raw".equals(render)) {
			forwardToDownloadServlet(result, req, resp);
			
			
		} else { // text
			respond(result, TEXT_HTML, resp);

			
		} 
	}

	/**
	 * 
	 * @param result
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forwardToDownloadServlet( Object result, HttpServletRequest req, HttpServletResponse resp ) 
			throws ServletException, IOException {
		req.setAttribute("File", result);
		ctx.getNamedDispatcher("DownloadServlet").forward(req, resp);
		
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
	 * @param e
	 * @return
	 */
	private Map<String, Object> getExcptionMap(Exception e) {
		Map<String, Object> errMap = new HashMap<String, Object>();
		errMap.put("error", e.getMessage());
		errMap.put("stacktrace", ExceptionUtils.getStackTrace(e));
		return errMap;
	}
	
	private Gson getGson() {
		return new GsonBuilder().setPrettyPrinting().create();
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
