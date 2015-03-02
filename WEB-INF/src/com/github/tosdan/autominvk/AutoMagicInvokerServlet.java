package com.github.tosdan.autominvk;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private void doServ(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String httpMethod = req.getMethod();
		logger.trace("Metodo HTTP = [{}]", httpMethod);
		AutoMagicAction action = getAction(req);
		logger.trace("{}", action);
		getParams(req);
		Object result = invoker.invoke(action, req, ctx);
		if (result instanceof RequestDispatcher) {
			((RequestDispatcher) result).forward(req, resp); 
		}
		sendResponse(result, resp);
	}

	private void getParams(HttpServletRequest req) {
		
	}
	
	private void sendResponse(Object result, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
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
		String requestURI = req.getRequestURI().toString();
		logger.trace("RequestedURI = [{}]", requestURI);
		String servletRelativeURI = requestURI.replace(ctxPath, "");
		logger.trace("Requested Servlet URI = [{}]", servletRelativeURI);
		String servletPath = req.getServletPath();
		logger.trace("Servlet Mapping = [{}]", servletPath);
		return new AutoMagicAction(servletRelativeURI, servletPath);
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ctx = config.getServletContext();
		String classpath = config.getInitParameter("CLASS_PATH");
		logger.debug("Class Path = [{}]", classpath);
		// Raccoglie tutte le classi da CLASS_PATH che siano annotate con AutoMagicInvokable
		AutoMagicClassCrawler crawler = new AutoMagicClassCrawler(classpath);
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
