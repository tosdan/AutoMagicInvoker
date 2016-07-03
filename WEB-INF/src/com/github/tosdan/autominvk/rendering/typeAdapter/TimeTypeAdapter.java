package com.github.tosdan.autominvk.rendering.typeAdapter;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class TimeTypeAdapter extends TypeAdapter<Time> {
	private static final String HH_MM = "HH:mm";
	private static final String HH_MM_SS_A = "hh:mm:ss a"; // Default di Gson
	private DateFormat FORMAT;
	public TimeTypeAdapter() { this(null); }
	public TimeTypeAdapter(String timeFormat) {
		initFormat(timeFormat);
	}
	
	private void initFormat( String timeFormat ) {
		timeFormat = timeFormat == null ? HH_MM : timeFormat;
		timeFormat = timeFormat.isEmpty() ? HH_MM_SS_A : timeFormat;			
		this.FORMAT = new SimpleDateFormat(timeFormat);
	}

	@Override public Time read( JsonReader reader ) throws IOException {
		if (this.FORMAT == null) {
			initFormat(null);
		}
		String json = reader.nextString();
		try {
			synchronized (FORMAT) {
				Date date = FORMAT.parse(json);
				return new java.sql.Time(date.getTime());
			}
		} catch (ParseException e) {
			throw new JsonSyntaxException(json, e);
		}
	}

	@Override public void write( JsonWriter writer, Time value ) throws IOException {
		if (this.FORMAT == null) {
			initFormat(null);
		}
		if (value == null) { writer.nullValue(); }
		else {
			synchronized (FORMAT) {
				writer.value(FORMAT.format(value));	
			} 
		}
	}
}
