package com.github.tosdan.autominvk.rendering.typeAdapter;

import static java.time.ZoneOffset.UTC;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class UtcLocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
	  private final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

	  @Override
	  public void write(JsonWriter out, LocalDateTime date) throws IOException {
	    if (date == null) {
	      out.nullValue();
	    } else {
	      String value = format(date, true, UTC_TIME_ZONE);
	      out.value(value);
	    }
	  }

	  @Override
	  public LocalDateTime read(JsonReader in) throws IOException {
	    try {
	      switch (in.peek()) {
	      case NULL:
	        in.nextNull();
	        return null;
	      default:
	        String date = in.nextString();
	        // Instead of using iso8601Format.parse(value), we use Jackson's date parsing
	        // This is because Android doesn't support XXX because it is JDK 1.6
	        return parse(date, new ParsePosition(0));
	      }
	    } catch (ParseException e) {
	      throw new JsonParseException(e);
	    }
	  }

	  /**
	   * Format date into yyyy-MM-ddThh:mm:ss[.sss][Z|[+-]hh:mm]
	   *
	   * @param localDate the date to format
	   * @param millis true to include millis precision otherwise false
	   * @param tz timezone to use for the formatting (GMT will produce 'Z')
	   * @return the date formatted as yyyy-MM-ddThh:mm:ss[.sss][Z|[+-]hh:mm]
	   */
	  private static String format(LocalDateTime localDate, boolean millis, TimeZone tz) {
	      Instant instant = localDate.toInstant(UTC);
	      Date date = Date.from(instant);
	      
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTimeInMillis(date.getTime());
	      
	      String retval = UtcCalendarTypeAdapter.format(calendar, millis, tz);
	      return retval;
	  }
	  
	  /**
	   * Parse a date from ISO-8601 formatted string. It expects a format
	   * [yyyy-MM-dd|yyyyMMdd][T(hh:mm[:ss[.sss]]|hhmm[ss[.sss]])]?[Z|[+-]hh:mm]]
	   *
	   * @param date ISO string to parse in the appropriate format.
	   * @param pos The position to start parsing from, updated to where parsing stopped.
	   * @return the parsed date
	   * @throws ParseException if the date is not in the appropriate format
	   */
	  private static LocalDateTime parse(String date, ParsePosition pos) throws ParseException {
		Calendar cal = UtcCalendarTypeAdapter.parse(date, pos);

		Instant instant = cal.toInstant();
		
		ZonedDateTime zonedDateTime = instant.atZone(UTC);
	    
		LocalDateTime retval = zonedDateTime.toLocalDateTime();
		return retval;
	  }
}
