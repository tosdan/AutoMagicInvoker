/**
 * 
 */
package com.github.tosdan.autominvk.apps.demo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.tosdan.autominvk.AutoMagicInvokable;
import com.github.tosdan.autominvk.AutoMagicInvokableAction;

/**
 * @author tosdan
 *
 */
@AutoMagicInvokable
public class HelloAutoMaricInvoker {

	/**
	 * 
	 */
	public HelloAutoMaricInvoker() {
		// TODO Auto-generated constructor stub
	}

	private HttpServletRequest req;
	private ServletContext ctx;
	private HttpSession session;

	@AutoMagicInvokableAction
	public Object get() throws Exception {
		System.out.println("HelloAutoMaricInvoker.get()");
		System.out.println("Attributo prova = "+ req.getAttribute("prova"));
		if (req != null) {
			System.out.println("Request params = " + req.getParameterMap());
			System.out.println("Attributo prova = "+ req.getAttribute("prova"));
		}
		if (ctx != null) {
			System.out.println("ServletContext non NULL = " + (ctx != null));
		}
		if (session != null) {
			System.out.println("HttpSession non NULL = " + (session != null));
		}
		return "dmeo/HelloAutoMaricInvoker.txt";
	}

	@AutoMagicInvokableAction
	public Object execute() throws Exception {
		System.out.println("HelloAutoMaricInvoker.execute()");
		return "demo";
	}
	

	@AutoMagicInvokableAction(method = "POST")
	public void noreturn() throws Exception {
		System.out.println("HelloAutoMaricInvoker.noreturn()");
	}
	
	@AutoMagicInvokableAction
	public RequestDispatcher spacher() throws Exception {
		System.out.println("HelloAutoMaricInvoker.spacher()");
		req.setAttribute("prova", "test");
		return req.getRequestDispatcher("/apps/mk/demo/HelloAutoMaricInvoker.get");
	}
	
	@AutoMagicInvokableAction
	public void conparam() throws Exception {
		System.out.println("HelloAutoMaricInvoker.conparam()");
	}
	
	public void noannotation() throws Exception {
		System.out.println("HelloAutoMaricInvoker.noannotation()");
	}
}
