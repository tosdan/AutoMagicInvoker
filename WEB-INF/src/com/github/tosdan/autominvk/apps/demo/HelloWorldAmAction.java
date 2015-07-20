package com.github.tosdan.autominvk.apps.demo;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.github.tosdan.autominvk.IamInvokable;
import com.github.tosdan.autominvk.IamInvokableAction;
import com.github.tosdan.utils.varie.HttpReuqestUtils;

@IamInvokable
public class HelloWorldAmAction {
	
	private HttpServletRequest req;

	public static class Range {
		private Integer min;
		private Integer max;
		public Integer getMin() { return min; }
		public void setMin( Integer min ) { this.min = min; }
		public Integer getMax() { return max; }
		public void setMax( Integer max ) { this.max = max; }
	}
	
	public static class HelloObject {
		private List<Range> ranges;
		private Range range;
		private String name;
		private String greet;
		private Boolean booleano;
		private List<String> multiplo;
		private String[] multiplo2;
		private HelloObject hello;
		private Integer year;
		private Double hours;
		public HelloObject() {}
		public List<Range> getRanges() { return ranges; }
		public void setRanges( List<Range> ranges ) { this.ranges = ranges; }
		public String getName() { return name; }
		public void setName( String name ) { this.name = name; }
		public String getGreet() { return greet; }
		public void setGreet( String greet ) { this.greet = greet; }
		public Integer getYear() { return year; } 
		public void setYear( Integer year ) { this.year = year; }
		public Double getHours() { return hours; }
		public void setHours( Double hours ) { this.hours = hours; }
		public HelloObject getHello() { return hello; } 
		public void setHello( HelloObject hello ) { this.hello = hello; }
		public List<String> getMultiplo() { return multiplo; } 
		public void setMultiplo( String[] multiplo2 ) { this.multiplo2 = multiplo2; }
		public String[] getMultiplo2() { return multiplo2; } 
		public void setMultiplo2( String[] multiplo2 ) { this.multiplo2 = multiplo2; }
		public Boolean getBooleano() { return booleano; }
		public void setBooleano( Boolean booleano ) { this.booleano = booleano; }
		public void setMultiplo( List<String> multiplo ) { this.multiplo = multiplo; }
		public Range getRange() { return range; }
		public void setRange( Range range ) { this.range = range; } 
		@Override
		public String toString() {
			return "HelloObject [name=" + name + ", greet=" + greet + ", booleano=" + booleano + ", multiplo=" + multiplo + ", multiplo2=" + Arrays.toString(multiplo2) + ", hello=" + hello + ", year=" + year +
					", hours=" + hours + ", range="+ range + ", ranges="+ ranges + "]";
		}
	}
	
	public HelloWorldAmAction() {
		// TODO Auto-generated constructor stub
	}
	
	@IamInvokableAction(mime = "application/json", render = "json", reqMethod = "post")
	public Object post() {
		HelloObject retval = null;
		
		retval = HttpReuqestUtils.buildBeanFromRequest(req, HelloObject.class);				
		
		return retval;
	}
	

	@IamInvokableAction(mime = "application/json", render = "json", reqMethod = "get")
	public Object get(HelloObject hello) {
		System.out.println(hello);
		return hello;
	}
}
