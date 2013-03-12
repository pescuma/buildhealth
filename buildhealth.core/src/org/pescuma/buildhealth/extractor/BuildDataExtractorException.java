package org.pescuma.buildhealth.extractor;

public class BuildDataExtractorException extends RuntimeException {
	
	private static final long serialVersionUID = -3090392318536619387L;
	
	public BuildDataExtractorException() {
		super();
	}
	
	public BuildDataExtractorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BuildDataExtractorException(String message) {
		super(message);
	}
	
	public BuildDataExtractorException(Throwable cause) {
		super(cause);
	}
	
}
