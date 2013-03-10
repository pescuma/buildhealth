package org.pescuma.buildhealth.core;

public class FinalReport implements Report {
	
	private final BuildStatus status;
	private final String name;
	private final String value;
	private final String description;
	
	public FinalReport(BuildStatus status, String name, String value, String description) {
		this.status = status;
		this.name = name;
		this.value = value;
		this.description = description;
	}
	
	public BuildStatus getStatus() {
		return status;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getDescription() {
		return description;
	}
	
}
