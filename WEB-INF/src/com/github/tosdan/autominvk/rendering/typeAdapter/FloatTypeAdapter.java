package com.github.tosdan.autominvk.rendering.typeAdapter;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Type adapter per consentire a {@link Gson} di effettuare il parse di cifre decimali con separatore virgola ","
 * @author Daniele
 *
 */
public class FloatTypeAdapter extends TypeAdapter<Float> {
	@Override public Float read(JsonReader reader) throws IOException {
		Float retval = null;
		if (reader.peek() == JsonToken.NULL) { reader.nextNull(); }
		String stringValue = reader.nextString();
		try { 
			Float value = Float.valueOf(stringValue.replace(",", "."));
			retval = value;
		} catch (NumberFormatException e) { }
		return retval;
	}
	@Override public void write(JsonWriter writer, Float value) throws IOException {
		if (value == null) { writer.nullValue(); }
		else { writer.value(value.toString().replace(".", ",")); }
	}
}
