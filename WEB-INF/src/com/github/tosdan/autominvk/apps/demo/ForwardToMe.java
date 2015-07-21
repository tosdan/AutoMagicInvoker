package com.github.tosdan.autominvk.apps.demo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ForwardToMe extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8061870299409816697L;

	@Override protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		System.out.println("ForwardToMe.doGet()");
		doAction(req, resp);
	}
	
	@Override protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		System.out.println("ForwardToMe.doPost()");
		doAction(req, resp);
	}

	private void doAction( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		resp.getWriter().println("FORWARDED HERE");
		
	}
}
