package com.aiinspector.exception;

public class AIException extends RuntimeException{	
	private static final long serialVersionUID = -1912302627316028943L;
	private String url;

	public AIException() {
		super();
	}

	public AIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AIException(String message, Throwable cause) {
		super(message, cause);
	}

	public AIException(String message) {
		super(message);
	}
	
	public AIException(String message, String url) {
		super(message);
		this.url = url;
	}

	public AIException(Throwable cause) {
		super(cause);
	}

	public String getUrl() {
		return url;
	}

}
