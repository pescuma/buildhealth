package org.pescuma.buildhealth.prefs;

public class PreferencesException extends RuntimeException {
	
	private static final long serialVersionUID = 7495704715322194002L;
	
	public PreferencesException() {
		super();
	}
	
	public PreferencesException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PreferencesException(String message) {
		super(message);
	}
	
	public PreferencesException(Throwable cause) {
		super(cause);
	}
	
}
