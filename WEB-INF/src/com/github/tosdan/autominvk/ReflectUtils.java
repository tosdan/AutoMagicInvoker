package com.github.tosdan.autominvk;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tosdan.utils.varie.HttpReuqestUtils;

public class ReflectUtils {

	private final static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);
	

	public static Object[] getArgs(HttpServletRequest req, Method method) {
		Parameter[] params = method.getParameters();
		Object[] args = new Object[params.length];
		Parameter p;
		for (int i = 0 ; i < params.length ; i++) {
			p = params[i];
			args[i] = HttpReuqestUtils.buildBeanFromRequest(req, p.getType());
			System.out.println("ReflectUtils.getArgs()="+ args[i]);
		}
		return args;
	}

	/*
	private Object getArgInstance(Parameter p, HttpServletRequest req) {
		Object arg = null;
		try {
			
			arg = p.getType().newInstance();
			setInstanceField(arg);
			
		} catch ( InstantiationException e ) {
			e.printStackTrace();
		} catch ( IllegalAccessException e ) {
			e.printStackTrace();
		}
		return arg;
	}



	public void setInstanceField(Object instance) 
			throws IllegalAccessException {

		Class<? extends Object> instanceClass = instance.getClass();
		Class<?> targetType;
		Object value;
		String fieldName;
		String[] reqParamValues;
		logger.debug("Classe istanza target [{}]", instanceClass.getName());
		Field[] fields = FieldUtils.getAllFields(instanceClass);
		
		for (Field f : fields) {
			targetType = f.getType();
			fieldName = f.getName();
			
			reqParamValues = req.getParameterValues(fieldName);
			
			logger.debug("Campo [{}] di tipo destinazione [{}]", fieldName, targetType.getName());
			if (reqParamValues != null) {
			logger.debug("con valore [{}]", Arrays.asList(reqParamValues));

				if (targetType.isAssignableFrom(instanceClass)) {
					logger.debug("Ignorato! Riferimenti circolari non supportati.");
					continue;
				}
				
				value = castToType(reqParamValues, targetType);
				FieldUtils.writeDeclaredField(instance, fieldName, value, true);
			}
		}
	}



	private Object castToType(String[] reqVal, Class<?> type) {
		Object retval = null;  
		
		// Se non è una stringa e non è nessuno dei seguenti lanverà un'eccezione e bisognerà aggiungere un if
		if (type.isAssignableFrom(Integer.class)) {
			logger.debug("Parsing as [{}]", Integer.class);
			retval = Integer.parseInt(reqVal[0]);
			
		} else if (type.isAssignableFrom(int.class)) {
			logger.debug("Parsing as [{}]", int.class);
			retval = Integer.parseInt(reqVal[0]);
			
		} else if (type.isAssignableFrom(Long.class)) {
			logger.debug("Parsing as [{}]", Long.class);
			retval = Long.parseLong(reqVal[0]);
			
		} else if (type.isAssignableFrom(BigDecimal.class)) {
			logger.debug("Parsing as [{}]", BigDecimal.class);
			retval = BigDecimal.valueOf(Double.parseDouble(reqVal[0].replace(",", ".")));
			
		} else if (type.isAssignableFrom(BigInteger.class)) {
			logger.debug("Parsing as [{}]", BigInteger.class);
			retval = BigInteger.valueOf(Long.parseLong(reqVal[0]));
			
		} else if (type.isAssignableFrom(long.class)) {
			logger.debug("Parsing as [{}]", long.class);
			retval = Long.parseLong(reqVal[0]);
			
		} else if (type.isAssignableFrom(Float.class)) {
			logger.debug("Parsing as [{}]", Float.class);
			retval = Float.parseFloat(reqVal[0].replace(",", "."));
			
		} else if (type.isAssignableFrom(float.class)) {
			logger.debug("Parsing as [{}]", float.class);
			retval = Float.parseFloat(reqVal[0].replace(",", "."));
			
		} else if (type.isAssignableFrom(Double.class)) {
			logger.debug("Parsing as [{}]", Double.class);
			retval = Double.parseDouble(reqVal[0].replace(",", "."));
			
		} else if (type.isAssignableFrom(double.class)) {
			logger.debug("Parsing as [{}]", double.class);
			retval = Double.parseDouble(reqVal[0].replace(",", "."));
			
		} else if (type.isAssignableFrom(Boolean.class)) {
			logger.debug("Parsing as [{}]", Boolean.class);
			retval = Boolean.parseBoolean(reqVal[0]);
			
		} else if (type.isAssignableFrom(boolean.class)) {
			logger.debug("Parsing as [{}]", boolean.class);
			retval = Boolean.parseBoolean(reqVal[0]);
			
		} else if (type.isAssignableFrom(String.class)){
			retval = reqVal[0];
			logger.debug("Parsing as [{}]", String.class);
			
		} else if (type.isAssignableFrom(List.class)) {
			logger.debug("Parsing as [{}]", List.class);
			retval = Arrays.asList(reqVal);
			
		} else if (type.isAssignableFrom(String[].class)) {
			logger.debug("Parsing as [{}]", String[].class);
			retval = reqVal;
			
		} else {
			logger.debug("Tipo sconosciuto! Impossibile fare il parse.");
		}
			
		return retval;
	}
	*/
}
