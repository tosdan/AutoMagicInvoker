/**
 * 
 */
package com.github.tosdan.autominvk.apps;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.tosdan.autominvk.IamInvokable;
import com.github.tosdan.autominvk.IamInvokableAction;

/**
 * @author tosdan
 *
 */
@IamInvokable(value = "")
public class CopyOfHelloAmAction {

	/**
	 * 
	 */
	public CopyOfHelloAmAction() {
		// TODO Auto-generated constructor stub
	}

	private HttpServletRequest req;
	private ServletContext ctx;
	private HttpSession session;
	
	class Retval {
		private String ciao = "oaic";
		private String test = "tset";
	}

	@IamInvokableAction()
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
//		return "demo/HelloAutoMaricInvoker.txt";
		return new Retval();
	}

	@IamInvokableAction
	public Object execute() throws Exception {
		System.out.println("HelloAutoMaricInvoker.execute()");
		return "demo";
	}
	

	@IamInvokableAction(reqMethod = "POST")
	public void noreturn() throws Exception {
		System.out.println("HelloAutoMaricInvoker.noreturn()");
	}
	
	@IamInvokableAction(alias = "")
	public RequestDispatcher spatcher() throws Exception {
		System.out.println("HelloAutoMaricInvoker.spatcher()");
		req.setAttribute("prova", "test");
		return req.getRequestDispatcher("hello%7Ejson");
	}
	
	@IamInvokableAction
	public void conparam() throws Exception {
		System.out.println("HelloAutoMaricInvoker.conparam()");
	}
	
	public void noannotation() throws Exception {
		System.out.println("HelloAutoMaricInvoker.noannotation()");
	}
}
