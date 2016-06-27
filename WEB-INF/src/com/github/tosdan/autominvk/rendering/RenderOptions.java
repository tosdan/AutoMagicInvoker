package com.github.tosdan.autominvk.rendering;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RenderOptions {
	private String gsonDateFormat;
	private String gsonTimeFormat;
	private boolean prettyPrinting;
	private boolean annotationExclusionStrategy;
	@Override public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE); }
	public RenderOptions() {
		this.prettyPrinting = true;
		this.annotationExclusionStrategy = true;
	}	
	public String getGsonDateFormat() {
		return gsonDateFormat;
	}
	public void setGsonDateFormat( String gsonDateFormat ) {
		this.gsonDateFormat = gsonDateFormat;
	}
	public String getGsonTimeFormat() {
		return gsonTimeFormat;
	}
	public void setGsonTimeFormat( String gsonTimeFormat ) {
		this.gsonTimeFormat = gsonTimeFormat;
	}
	public boolean isPrettyPrinting() {
		return prettyPrinting;
	}
	public void setPrettyPrinting( boolean prettyPrinting ) {
		this.prettyPrinting = prettyPrinting;
	}
	public boolean isAnnotationExclusionStrategy() {
		return annotationExclusionStrategy;
	}
	public void setAnnotationExclusionStrategy( boolean annotationExclusionStrategy ) {
		this.annotationExclusionStrategy = annotationExclusionStrategy;
	}	
}
