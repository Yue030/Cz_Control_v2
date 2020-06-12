package com.yue.czcontrol.exception;

public class NameNotFoundException extends Exception{
	
	private static final long serialVersionUID = 1L;

	/**
	 * When the NameNotFound
	 */
	public NameNotFoundException() {
		super();
	}
	
	/**
	 * When the NameNotFound
	 * @param msg The message
	 */
	public NameNotFoundException(String msg) {
		super(msg);
	}
	
	/**
	 * When the NameNotFound
	 * @param msg The message
	 * @param cause Cause by
	 */
	public NameNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
