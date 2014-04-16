package org.pescuma.buildhealth.core;

import java.util.List;

public class BuildReport extends Report {
	
	private final String prefix;
	private final String suffix;
	private final List<Report> sourcesOfProblems;
	
	public BuildReport(BuildStatus status, String name, String value, String prefix, String suffix,
			List<Report> sourcesOfProblems, List<Report> children) {
		super(status, name, value, null, null, children);
		this.prefix = prefix;
		this.suffix = suffix;
		this.sourcesOfProblems = sourcesOfProblems;
	}
	
	public BuildReport(BuildStatus status, String name, String value, List<Report> sourcesOfProblems,
			Report... children) {
		super(status, name, value, null, null, children);
		this.prefix = "";
		this.suffix = "";
		this.sourcesOfProblems = sourcesOfProblems;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public List<Report> getSourcesOfProblems() {
		return sourcesOfProblems;
	}
	
}
