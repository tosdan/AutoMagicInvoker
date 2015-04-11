package com.github.tosdan.autominvk;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Classe di utilità per trasformare un {@link Object} in una mappa.
 * @author Daniele
 *
 */
public class MapMaker {

	private Object obj;

	/**
	 * 
	 * @param obj Oggetto da trasformare in mappa.
	 */
	public MapMaker(Object obj) {
		this.obj = obj;
	}

	/**
	 * Popola una mappa con chiavi e valori dei campi passati come parametri, 
	 * mantenendo l'ordine specificaito nel passaggio dei parametri.
	 * @param fields lista dei campi da estrarre dall'oggetto passato
	 * @return Mappa da <em>String</em> ad <em>Object</em>
	 * @throws IllegalAccessException Se l'oggetto non contiene uno dei nomi passati nella lista <em>fields</em>
	 */
	public Map<String, Object> getMap(String ...fields) throws IllegalAccessException {
		Map<String, Object> retval = new LinkedHashMap<String, Object>();
		
		for(String field : fields) {
			Object val = FieldUtils.readDeclaredField(obj, field, true);
			retval.put(field, val);
		}
		
		return retval;
	}
	
	/**
	 * Popola una mappa le cui chiavi sono i nomi dei campi passati e i cui valori sono ottenuti
	 * dagli ominimi campi dell'oggetto passato.
	 * @param obj oggetto da cui estrarre i valori
	 * @param fields lista dei campi da estrarre dall'oggetto passato
	 * @return
	 * @throws IllegalAccessException
	 */
	public static Map<String, Object> getMap(Object obj, String ...fields) throws IllegalAccessException {
		return new MapMaker(obj).getMap(fields);
	}
}
