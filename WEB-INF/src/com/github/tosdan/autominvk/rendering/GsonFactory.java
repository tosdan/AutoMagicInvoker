package com.github.tosdan.autominvk.rendering;

import java.sql.Time;

import com.github.tosdan.autominvk.rendering.render.JsonAnnotationExclusionStrategy;
import com.github.tosdan.autominvk.rendering.typeAdapter.DoubleTypeAdapter;
import com.github.tosdan.autominvk.rendering.typeAdapter.FloatTypeAdapter;
import com.github.tosdan.autominvk.rendering.typeAdapter.TimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {
	public static Gson getGson(RenderOptions renderOptions) {
		GsonBuilder builder = new GsonBuilder()
								.registerTypeAdapter(Time.class, new TimeTypeAdapter(renderOptions.getGsonTimeFormat()))
								.registerTypeAdapter(Double.class, new DoubleTypeAdapter())
								.registerTypeAdapter(Float.class, new FloatTypeAdapter());
		
		if (renderOptions.getGsonDateFormat() != null) {
			builder.setDateFormat(renderOptions.getGsonDateFormat());
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
