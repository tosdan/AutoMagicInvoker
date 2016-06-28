package com.github.tosdan.autominvk.rendering.typeAdapter;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
/**
 * Type adapter per consentire a {@link Gson} di effettuare il parse di cifre decimali con separatore virgola ","
 * @author Daniele
 *
 */
public class DoubleTypeAdapter extends TypeAdapter<Double> {
	@Override public Double read(JsonReader reader) throws IOException {
		Double retval = null;
        if (reader.peek() == JsonToken.NULL) { reader.nextNull(); }
        String json = reader.nextString();
        try { 
        	Double value = Double.valueOf(json.replace(",", "."));
            retval = value;
        } catch (NumberFormatException e) {
			throw new JsonSyntaxException(json, e);
        }
        return retval;
    }
	@Override public void write(JsonWriter writer, Double value) throws IOException {
        if (value == null) { writer.nullValue(); }
        else { writer.value(value.toString().replace(".", ",")); }
    }
}
