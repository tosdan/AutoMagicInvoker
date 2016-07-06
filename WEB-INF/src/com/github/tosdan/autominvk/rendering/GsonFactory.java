package com.github.tosdan.autominvk.rendering;

import java.util.Date;

import com.github.tosdan.autominvk.rendering.render.JsonAnnotationExclusionStrategy;
import com.github.tosdan.autominvk.rendering.typeAdapter.DoubleTypeAdapter;
import com.github.tosdan.autominvk.rendering.typeAdapter.FloatTypeAdapter;
import com.github.tosdan.autominvk.rendering.typeAdapter.TimeTypeAdapter;
import com.github.tosdan.autominvk.rendering.typeAdapter.UtcDateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {
	public static Gson getGson(RenderOptions renderOptions) {
		if (renderOptions == null) { renderOptions = new RenderOptions(); }
		
		GsonBuilder builder = new GsonBuilder()
								.registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter(renderOptions.getGsonTimeFormat()))
								.registerTypeAdapter(java.sql.Date.class, new UtcDateTypeAdapter())
								.registerTypeAdapter(java.sql.Timestamp.class, new UtcDateTypeAdapter())
								.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
								.registerTypeAdapter(Float.class, new FloatTypeAdapter());
		
		
		String gsonDateFormat = renderOptions.getGsonDateFormat();
		if (gsonDateFormat != null) {
			builder.setDateFormat(gsonDateFormat);
		}		
		if (gsonDateFormat == null || gsonDateFormat.isEmpty()) {
			builder.registerTypeAdapter(Date.class, new UtcDateTypeAdapter());
		}
		
		if (renderOptions.isPrettyPrinting()) {
			builder.setPrettyPrinting();
		}
		
		if (renderOptions.isAnnotationExclusionStrategy()) {
			builder.addSerializationExclusionStrategy(new JsonAnnotationExclusionStrategy());
		}		
		
		Gson gson = builder.create();
		
		return gson;
	}	
}
