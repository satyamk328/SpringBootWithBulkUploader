package com.test.exception;

public abstract class BaseException extends RuntimeException {

	
	private static final long serialVersionUID = 1L;


	public BaseException() {
	}

	
	public BaseException(String message) {
		super(message);
	}

	
	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	
	public BaseException(String message, Throwable cause, boolean arg2, boolean arg3) {
		super(message, cause, arg2, arg3);
	}

}
