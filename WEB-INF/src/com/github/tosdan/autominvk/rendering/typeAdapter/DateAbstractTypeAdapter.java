package com.github.tosdan.autominvk.rendering.typeAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public abstract class DateAbstractTypeAdapter extends TypeAdapter<Date> {
    private DateFormat FORMAT;
	public DateAbstractTypeAdapter(String dateFormat) {
		initFormat(dateFormat);
	}

	private void initFormat(String dateFormat) {
		this.FORMAT = new SimpleDateFormat(dateFormat);
	}
    @Override
    public void write(JsonWriter writer, Date value) throws IOException {
        if (value == null) { writer.nullValue(); } 
        else {
        	synchronized (FORMAT) {
            	writer.value(FORMAT.format(value));
			} 
        }
    }

    @Override
    public Date read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        String json = reader.nextString();
        try {
			synchronized (FORMAT) {
				return FORMAT.parse(json);
			}
        } catch (ParseException e) {
			throw new JsonSyntaxException(json, e);
        }
    }
}
