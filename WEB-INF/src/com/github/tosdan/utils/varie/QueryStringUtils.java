package com.github.tosdan.utils.varie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryStringUtils {

	@SuppressWarnings( "unchecked" )
	public static Map<String, Object> parse(String queryString) {
		Map<String, Object> retval = new HashMap<String, Object>();		
		List<String> temp;
		Object prevVal;
	    if (queryString != null) {
			for( String pair : queryString.split("&") ) {

				int eq = pair.indexOf("=");
				if ( eq > 0 ) {
					String key = decode(pair.substring(0, eq));
					String value = decode(pair.substring(eq + 1));

					if ( !key.isEmpty() && !value.isEmpty() ) {

						prevVal = retval.get(key);
						if ( prevVal != null ) {
							if ( prevVal instanceof List ) {
								((List<String>) prevVal).add(value);
							} else {
								temp = new ArrayList<String>();
								temp.add(prevVal.toString());
								temp.add(value);
								retval.put(key, temp);
							}
						} else {
							retval.put(key, value);
						}
					}
				}
			} 
		}
		return retval;
	}

	public static String decode(String string) {
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch ( UnsupportedEncodingException e ) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
	    Map<String, Object> result = QueryStringUtils.parse("k1=v1"+"&k2"+"&k3=v3"+ "&k3"+ "&=" +"&k1=v4"+"&k1=v2"+"&k5=hello+%22world");
	    System.out.println("Result:" + result);
	}
}
