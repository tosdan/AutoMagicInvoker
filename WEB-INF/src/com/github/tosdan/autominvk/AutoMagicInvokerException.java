package com.github.tosdan.autominvk;

/**
 * 
 * @author tosdan
 *
 */
public class AutoMagicInvokerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9094397037015898340L;

	public AutoMagicInvokerException() {
		super();
	}
	
	public AutoMagicInvokerException( String message ) {
		super(message);
	}

	public AutoMagicInvokerException( Throwable cause ) {
		super(cause);
	}

	public AutoMagicInvokerException( String message, Throwable cause ) {
		super(message, cause);
	}


}
