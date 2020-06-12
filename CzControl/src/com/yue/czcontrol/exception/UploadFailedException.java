package com.yue.czcontrol.exception;

public class UploadFailedException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * Upload data failed
	 */
	public UploadFailedException() {
		super();
	}
	
	/**
	 * Upload data failed
	 * @param msg The message
	 */
	public UploadFailedException(String msg) {
		super(msg);
	}
	
	/**
	 * Upload data failed
	 * @param msg The message
	 * @param cause Cause by
	 */
	public UploadFailedException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
