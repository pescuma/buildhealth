package org.pescuma.buildhealth.core;

import java.util.List;

public class BuildReport extends Report {
	
	private final List<Report> sourcesOfProblems;
	
	public BuildReport(BuildStatus status, String name, String value, List<Report> sourcesOfProblems,
			List<Report> children) {
		super(status, name, value, false, children);
		this.sourcesOfProblems = sourcesOfProblems;
	}
	
	public BuildReport(BuildStatus status, String name, String value, List<Report> sourcesOfProblems,
			Report... children) {
		super(status, name, value, false, children);
		this.sourcesOfProblems = sourcesOfProblems;
	}
	
	public List<Report> getSourcesOfProblems() {
		return sourcesOfProblems;
	}
	
}
