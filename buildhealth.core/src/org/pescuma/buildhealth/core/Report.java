package org.pescuma.buildhealth.core;

public interface Report {
	
	BuildStatus getStatus();
	
	String getName();
	
	String getValue();
	
	String getDescription();
	
}
