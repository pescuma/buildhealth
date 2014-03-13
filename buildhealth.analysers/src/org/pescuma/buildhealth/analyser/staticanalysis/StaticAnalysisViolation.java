package org.pescuma.buildhealth.analyser.staticanalysis;

import java.util.List;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.utils.Location;

public class StaticAnalysisViolation extends Report {
	
	private final String language;
	private final String framework;
	private final List<Location> locations;
	private final String category;
	private final String message;
	private final String severity;
	private final String details;
	private final String url;
	
	public StaticAnalysisViolation(BuildStatus status, String language, String framework, List<Location> locations,
			String category, String message, String severity, String details, String url, String problemDescription) {
		super(status, locations.get(0).file, Integer.toString(locations.get(0).beginLine), createDescription(message,
				severity), problemDescription);
		
		this.language = language;
		this.framework = framework;
		this.locations = locations;
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
	
	public List<Location> getLocations() {
		return locations;
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
