package com.github.tosdan.autominvk.apps.demo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.github.tosdan.autominvk.IamInvokable;
import com.github.tosdan.autominvk.IamInvokableAction;
import com.github.tosdan.autominvk.ReflectUtils;
import com.github.tosdan.utils.varie.HttpReuqestUtils;
import com.github.tosdan.utils.varie.QueryStringUtils;
import com.google.gson.Gson;

@IamInvokable
public class HelloWorldAmAction {
	
	private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	private HttpServletRequest req;

	public static class HelloObject {
		private String name;
		private String greet;
		private boolean booleano;
		private List<String> multiplo;
		private String[] multiplo2;
		private HelloObject hello;
		private int year;
		private double hours;
		public HelloObject() {}
		public String getName() { return name; }
		public void setName( String name ) { this.name = name; }
		public String getGreet() { return greet; }
		public void setGreet( String greet ) { this.greet = greet; }
		public int getYear() { return year; } 
		public void setYear( int year ) { this.year = year; }
		public double getHours() { return hours; }
		public void setHours( double hours ) { this.hours = hours; }
		public HelloObject getHello() { return hello; } 
		public void setHello( HelloObject hello ) { this.hello = hello; }
		public List<String> getMultiplo() { return multiplo; } 
		public void setMultiplo( String[] multiplo2 ) { this.multiplo2 = multiplo2; }
		public String[] getMultiplo2() { return multiplo2; } 
		public void setMultiplo2( String[] multiplo2 ) { this.multiplo2 = multiplo2; }
		public boolean isBooleano() { return booleano; }
		public void setBooleano( boolean booleano ) { this.booleano = booleano; }
		public void setMultiplo( List<String> multiplo ) { this.multiplo = multiplo; }
		@Override
		public String toString() {
			return "HelloObject [name=" + name + ", greet=" + greet + ", booleano=" +
					booleano + ", multiplo=" + multiplo + ", multiplo2=" +
					Arrays.toString(multiplo2) + ", hello=" + hello + ", year=" + year +
					", hours=" + hours + "]";
		}
	}
	
	public HelloWorldAmAction() {
		// TODO Auto-generated constructor stub
	}
	
	@IamInvokableAction(mime = "application/json", render = "json", reqMethod = "post")
	public Object post() {
		HelloObject retval = null;
		Gson gson = new Gson();
		try {
			String requestBody = HttpReuqestUtils.parseRequestBody(req);
			String contentType = req.getContentType();
			if (APPLICATION_X_WWW_FORM_URLENCODED.equals(contentType)) {
				System.out.println("Content Type: "+contentType);
				Map<String, Object> map = QueryStringUtils.parse(requestBody);
				System.out.println("Map: "+map);
				String json = gson.toJson(map);
				System.out.println("Json: "+ json);
				retval = gson.fromJson(json, HelloObject.class);
			}
			System.out.println("requestBody: "+requestBody);
		} catch ( IOException e1 ) {
			e1.printStackTrace();
		}
		
		return retval;
	}

	@IamInvokableAction(mime = "application/json", render = "json", reqMethod = "get")
	public Object get() {
		ReflectUtils reflectUtil = new ReflectUtils(req);

		HelloObject hello = new HelloObject();
		try {
			reflectUtil.setInstanceField(hello);
			System.out.println(hello.toString());
		} catch ( IllegalAccessException e ) {
			e.printStackTrace();
		}
		
		return hello;
	}
}
