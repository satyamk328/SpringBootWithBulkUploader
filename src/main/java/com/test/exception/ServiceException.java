package com.test.exception;

public class ServiceException extends BaseException {

	private static final long serialVersionUID = -6854945379036729034L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}

	
	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
