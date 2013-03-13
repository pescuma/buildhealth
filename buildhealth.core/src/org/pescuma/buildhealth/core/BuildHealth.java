package org.pescuma.buildhealth.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.table.BuildDataTable;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;

public class BuildHealth {
	
	private BuildData table = new BuildDataTable();
	private List<BuildHealthAnalyser> analysers = new ArrayList<BuildHealthAnalyser>();
	
	public BuildHealth() {
		this(null);
	}
	
	public BuildHealth(File buildHealthHome) {
		// TODO Auto-generated constructor stub
	}
	
	public void startNewBuild() {
		// TODO Auto-generated method stub
	}
	
	public void addAnalyser(BuildHealthAnalyser analyser) {
		analysers.add(analyser);
	}
	
	public void extract(BuildDataExtractor extractor) {
		extractor.extractTo(table);
	}
	
	public Report generateReportSummary() {
		if (table.isEmpty())
			return null;
		
		List<Report> reports = new ArrayList<Report>();
		
		for (BuildHealthAnalyser analyser : analysers)
			reports.addAll(analyser.computeSimpleReport(table));
		
		if (reports.isEmpty())
			return null;
		
		BuildStatus status = Report.mergeBuildStatus(reports);
		
		return new Report(status, "Build", status.name(), null, reports);
	}
	
}
