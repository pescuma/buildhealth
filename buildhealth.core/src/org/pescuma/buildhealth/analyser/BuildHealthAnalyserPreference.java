package org.pescuma.buildhealth.analyser;

public class BuildHealthAnalyserPreference {
	
	private final String description;
	private final String[] key;
	private final String defVal;
	
	public BuildHealthAnalyserPreference(String description, String defVal, String... key) {
		this.description = description;
		this.key = key;
		this.defVal = defVal;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String[] getKey() {
		return key;
	}
	
	public String getDefVal() {
		return defVal;
	}
	
}
