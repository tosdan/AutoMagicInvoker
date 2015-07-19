/**
 * 
 */
package com.github.tosdan.autominvk.apps.demo;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
@IamInvokable
public class HelloAmAction {

	private HttpServletRequest req;
	private ServletContext ctx;
	private HttpSession session;
	
	class Retval {
		private String test;
		private String ciao;
		public Retval(String ciao, String test) {
			this.ciao = ciao;
			this.test = test;
		
		}
		public String getCiao() { return ciao; }
		public String getTest() { return test; }		
	}

	@IamInvokableAction(mime="application/json", render="json")
	public Object get() throws Exception {
		String ciao = "oaic";
		String test = "tset";
		System.out.println("HelloAutoMaricInvoker.get()*********************************** BEGIN");
		if (req != null) {
			System.out.println("Request params = " + req.getParameterMap());
			System.out.println("Attributo prova = "+ req.getAttribute("prova"));
			if (req.getAttribute("echo") != null) {
				System.out.println(ciao = (String)req.getAttribute("ciao"));
				System.out.println(test = (String)req.getAttribute("test"));	
			}
		}
		if (ctx != null) {
			System.out.println("ServletContext non NULL = " + (ctx != null));
		}
		if (session != null) {
			System.out.println("HttpSession non NULL = " + (session != null));
		}
		System.out.println("HelloAutoMaricInvoker.get()*********************************** END");
		return new Retval(ciao, test);
	}
	
	@IamInvokableAction(mime="application/json", render="json")
	public Object echo() throws Exception {
		Map<String, Object> retval = new HashMap<String, Object>();
		
		System.out.println("HelloAutoMaricInvoker.echo()*********************************** BEGIN");
		if (req != null) {
			@SuppressWarnings( "rawtypes" )
			Enumeration names = req.getParameterNames();
			while (names.hasMoreElements()) {
				Object name = names.nextElement();
				retval.put(name.toString(), req.getParameter(name.toString()));
			}
		}
		System.out.println("HelloAutoMaricInvoker.echo()*********************************** END");
		
		return retval;
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
	
	@IamInvokableAction(alias = "forward")
	public RequestDispatcher forwardToMySelf() throws Exception {
		System.out.println("HelloAutoMaricInvoker.spatcher()");
		req.setAttribute("ciao", "forward");
		req.setAttribute("test", "drawrof");
		req.setAttribute("echo", true);
		String tilde = "%7E";
		return req.getRequestDispatcher("hello.get"+tilde+"json");
	}
	
	@IamInvokableAction
	public void conparam() throws Exception {
		System.out.println("HelloAutoMaricInvoker.conparam()");
	}
	
	public void noannotation() throws Exception {
		System.out.println("HelloAutoMaricInvoker.noannotation()");
	}
}
