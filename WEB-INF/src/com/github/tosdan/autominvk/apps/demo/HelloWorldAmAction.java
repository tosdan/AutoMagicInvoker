package com.github.tosdan.autominvk.apps.demo;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import com.github.tosdan.autominvk.AutoMagicHttpError;
import com.github.tosdan.autominvk.IamInvokable;
import com.github.tosdan.autominvk.IamInvokableAction;
import com.github.tosdan.autominvk.rendering.render.Default;
import com.github.tosdan.autominvk.rendering.render.Json;
import com.github.tosdan.autominvk.rendering.render.Json2;
import com.github.tosdan.autominvk.rendering.render.JsonP;
import com.github.tosdan.autominvk.rendering.typeAdapter.TimeTypeAdapter;
import com.github.tosdan.autominvk.rendering.typeAdapter.UtcDateTypeAdapter;
import com.github.tosdan.autominvk.rendering.typeAdapter.DateDdMmYyyySlashedTypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.sun.xml.internal.txw2.IllegalAnnotationException;

@IamInvokable
public class HelloWorldAmAction {
	
//	private HttpServletRequest req;

	private ServletContext ctx;
	
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
		private Date data;
		private Timestamp timestamp;
		@JsonAdapter(DateDdMmYyyySlashedTypeAdapter.class)
		private java.sql.Date dataSql;
		@JsonAdapter(UtcDateTypeAdapter.class)
		private Date data2;
		@JsonAdapter(UtcDateTypeAdapter.class)
		private Date data3;
		@JsonAdapter(TimeTypeAdapter.class)
		private Time time;
		private Time time2;
		private boolean checkbox;
		public boolean isCheckbox() { return checkbox; } 
		public void setCheckbox( boolean checkbox ) { this.checkbox = checkbox; }
		public Date getData() { return data; }
		public void setData( Date data ) { this.data = data; }
		public Date getData2() { return data2; }
		public void setData2( Date data2 ) { this.data2 = data2; }
		public Date getData3() { return data3; }
		public void setData3( Date data3 ) { this.data3 = data3; }
		public Range getRange() { return range; }
		public void setRange( Range range ) { this.range = range; } 
		public List<Range> getRanges() { return ranges; }
		public void setRanges( List<Range> ranges ) { this.ranges = ranges; }
		public String getName() { return name; }
		public void setName( String name ) { this.name = name; }
		public Time getTime() { return time; }
		public void setTime( Time time ) { this.time = time; }
		public Time getTime2() { return time2; }
		public void setTime2( Time time2 ) { this.time2 = time2; }
		public java.sql.Date getDataSql() { return dataSql; }
		public void setDataSql( java.sql.Date dataSql ) { this.dataSql = dataSql; }
		public Timestamp getTimestamp() { return timestamp; }
		public void setTimestamp( Timestamp timestamp ) { this.timestamp = timestamp; }
		@Override public String toString() { return "HelloObject [ranges=" + ranges + ", range=" + range + ", name=" + name + "]"; }
		
	}
	
	public static class HelloObjectExt extends HelloObject {
		private String greet;
		@JsonAdapter(UtcDateTypeAdapter.class)
		private Date unaData;
		private Boolean booleano;
		private List<String> multiplo;
		private String[] multiplo2;
		private HelloObjectExt hello;
		private Integer year;
		private Double hours;
		public HelloObjectExt() { this.unaData = new Date(); }
		public void setUnaData( Date unaData ) { this.unaData = unaData; }
		public Date getUnaData() { return unaData; }
		public String getGreet() { return greet; }
		public void setGreet( String greet ) { this.greet = greet; }
		public Integer getYear() { return year; } 
		public void setYear( Integer year ) { this.year = year; }
		public Double getHours() { return hours; }
		public void setHours( Double hours ) { this.hours = hours; }
		public HelloObjectExt getHello() { return hello; } 
		public void setHello( HelloObjectExt hello ) { this.hello = hello; }
		public List<String> getMultiplo() { return multiplo; } 
		public void setMultiplo( String[] multiplo2 ) { this.multiplo2 = multiplo2; }
		public String[] getMultiplo2() { return multiplo2; } 
		public void setMultiplo2( String[] multiplo2 ) { this.multiplo2 = multiplo2; }
		public Boolean getBooleano() { return booleano; }
		public void setBooleano( Boolean booleano ) { this.booleano = booleano; }
		public void setMultiplo( List<String> multiplo ) { this.multiplo = multiplo; }
		@Override
		public String toString() {
			return "HelloObject [name=" + super.name + ", greet=" + greet + ", booleano=" + booleano + ", multiplo=" + multiplo + ", multiplo2=" + Arrays.toString(multiplo2) + ", hello=" + hello + ", year=" + year +
					", hours=" + hours + ", range="+ super.range + ", ranges="+ super.ranges + "]";
		}
	}
	
	public HelloWorldAmAction() {
		// TODO Auto-generated constructor stub
	}
	
	@IamInvokableAction(mime = "application/json", render = Json2.class, reqMethod = "post", gsonDateFormat="yyyy-MM-dd", gsonTimeFormat="HH:mm:ss")
	public Object post(HelloObjectExt helloExt, HelloObject hello) {
		Map<String, Object> retval = new HashMap<String, Object>();
		hello.setDataSql(new java.sql.Date(new Date().getTime()));
		hello.setTimestamp(new Timestamp(new Date().getTime()));
		retval.put("hello", hello);	
		retval.put("helloExt", helloExt);	
		System.out.println("HelloWorldAmAction.post()");
		System.out.println("Hello Ext: " + helloExt);
		System.out.println("Hello: " + hello);
		System.out.println("Hello DATA  : " + hello.getData());
		System.out.println("Hello DATA-2: " + hello.getData2());
		System.out.println("Hello DATA-3: " + hello.getData3());
		return retval;
	}

	@IamInvokableAction(mime = "application/json", reqMethod = "get")
	public Object get(HelloObjectExt hello) {
		System.out.println("HelloWorldAmAction.get()");
		System.out.println(hello);
		return hello;
	}
	
	
	@IamInvokableAction(mime = "application/json", render = Default.class, reqMethod = "get")
	public Object getDefault(HelloObjectExt hello) {
		System.out.println("HelloWorldAmAction.getDefault()");
		System.out.println(hello);
		return hello;
	}
	
	
	@IamInvokableAction(mime = "application/json", render = Json2.class , reqMethod = "get")
	public Object getJson(HelloObjectExt hello) {
		System.out.println("HelloWorldAmAction.getJson()");
		System.out.println(hello);
		return hello;
	}
	
	
	@IamInvokableAction(mime = "application/json", render = JsonP.class, reqMethod = "get")
	public Object getJsonP(HelloObjectExt hello) {
		System.out.println("HelloWorldAmAction.getJsonP()");
		System.out.println(hello);
		return hello;
	}
	
	
	@IamInvokableAction(mime = "application/json", render = Json2.class, reqMethod = "get")
	public Object exception() {
		System.out.println("HelloWorldAmAction.exception()");
		throw new IllegalAnnotationException("Eccezione");
	}
	
	
	@IamInvokableAction(mime = "application/json", render = Json2.class, reqMethod = "get")
	public Object error() {
		System.out.println("HelloWorldAmAction.error()");
		return new AutoMagicHttpError(400, "Errore demo.");
	}

	
	@IamInvokableAction(reqMethod = "get")
	public Object forward() {
		System.out.println("HelloWorldAmAction.forward()");
		RequestDispatcher dispatcher = ctx.getNamedDispatcher("ForwardToMe");
		return dispatcher;
	}
}
