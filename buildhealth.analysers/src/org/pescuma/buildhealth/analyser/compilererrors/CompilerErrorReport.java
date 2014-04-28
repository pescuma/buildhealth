package org.pescuma.buildhealth.analyser.compilererrors;

import java.util.List;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.utils.Location;

public class CompilerErrorReport extends Report {
	
	private final String language;
	private final String framework;
	private final List<Location> locations;
	private final String category;
	private final String message;
	private final String details;
	
	public CompilerErrorReport(BuildStatus status, String language, String framework, List<Location> locations,
			String category, String message, String details, String problemDescription) {
		super(status, locations.get(0).file, Integer.toString(locations.get(0).beginLine), message, problemDescription);
		
		this.language = language;
		this.framework = framework;
		this.locations = locations;
		this.category = category;
		this.message = message;
		this.details = details;
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
	
	public String getDetails() {
		return details;
	}
	
}
