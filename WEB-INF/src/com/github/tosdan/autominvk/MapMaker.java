package com.github.tosdan.autominvk;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;

public class MapMaker {

	private Object obj;

	public MapMaker(Object obj) {
		this.obj = obj;
	}

	/**
	 * Popola una mappa con i chiavi e valori dei campi passati come parametri, mantenendo l'ordine specificaito nel passaggio dei parametri.
	 * @param fields
	 * @return
	 * @throws IllegalAccessException
	 */
	public Map<String, Object> getMap(String ...fields) throws IllegalAccessException {
		Map<String, Object> retval = new LinkedHashMap<String, Object>();
		
		for(String field : fields) {
			Object val = FieldUtils.readDeclaredField(obj, field, true);
			retval.put(field, val);
		}
		
		return retval;
	}
	
	public static Map<String, Object> getMap(Object obj, String ...fields) throws IllegalAccessException {
		return new MapMaker(obj).getMap(fields);
	}
}
