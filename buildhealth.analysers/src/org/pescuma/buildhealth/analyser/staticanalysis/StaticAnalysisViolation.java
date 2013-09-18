package org.pescuma.buildhealth.analyser.staticanalysis;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

public class StaticAnalysisViolation extends Report {
	
	private final String language;
	private final String framework;
	private final String filename;
	private final String line;
	private final String category;
	private final String message;
	private final String severity;
	private final String details;
	private final String url;
	
	public StaticAnalysisViolation(BuildStatus status, String language, String framework, String filename, String line,
			String category, String message, String severity, String details, String url) {
		super(status, filename, line, createDescription(message, severity));
		
		this.language = language;
		this.framework = framework;
		this.filename = filename;
		this.line = line;
		this.category = category;
		this.message = message;
		this.severity = severity;
		this.details = details;
		this.url = url;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public String getFramework() {
		return framework;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getLine() {
		return line;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getSeverity() {
		return severity;
	}
	
	public String getDetails() {
		return details;
	}
	
	public String getUrl() {
		return url;
	}
	
	private static String createDescription(String message, String severity) {
		StringBuilder out = new StringBuilder();
		
		boolean hasMessage = !message.isEmpty();
		if (hasMessage)
			out.append(message);
		
		if (!severity.isEmpty()) {
			if (hasMessage)
				out.append(" (");
			out.append(severity);
			if (hasMessage)
				out.append(")");
		}
		
		return out.toString();
	}
	
}
