package com.github.tosdan.autominvk.rendering.typeAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class DateTypeAdapter extends TypeAdapter<Date> {
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
    private DateFormat FORMAT;
	public DateTypeAdapter(String dateFormat) {
		initFormat(dateFormat);
	}

	private void initFormat(String dateFormat) {
		dateFormat = dateFormat == null ? DD_MM_YYYY : dateFormat;
		this.FORMAT = new SimpleDateFormat(dateFormat);
	}
//    @Override
    public void $write(JsonWriter out, Date value) throws IOException {
        if (value == null) { out.nullValue(); } 
        else { out.value(FORMAT.format(value)); }
    }

//    @Override
    public Date $read(JsonReader reader) throws IOException {
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
    
    
    
    
    
    
	private final DateFormat enUsFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
	private final DateFormat localFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);

	@Override
	public Date read( JsonReader in ) throws IOException {
		if ( in.peek() == JsonToken.NULL ) {
			in.nextNull();
			return null;
		}
		return deserializeToDate(in.nextString());
	}

	private synchronized Date deserializeToDate(String json) {
		try {
			return localFormat.parse(json);
		} catch ( ParseException ignored ) {}
		
		try {
			return enUsFormat.parse(json);
		} catch ( ParseException ignored ) {}
		
		try {
			return ISO8601Utils.parse(json, new ParsePosition(0));
		} catch ( ParseException e ) {
			throw new JsonSyntaxException(json, e);
		}
	}

	@Override
	public synchronized void write( JsonWriter out, Date value ) throws IOException {
		if ( value == null ) {
			out.nullValue();
			return;
		}
		String dateFormatAsString = enUsFormat.format(value);
		out.value(dateFormatAsString);
	}
}
