package org.pescuma.buildhealth.core;

import java.util.ArrayList;
import java.util.List;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.table.BuildDataTable;
import org.pescuma.buildhealth.extractor.BuildDataExtractor;


public class BuildHealth {
	
	private BuildData table = new BuildDataTable();
	private List<BuildHealthAnalyser> analysers = new ArrayList<BuildHealthAnalyser>();
	
	public void addAnalyser(BuildHealthAnalyser analyser) {
		analysers.add(analyser);
	}
	
	public void extract(BuildDataExtractor extractor) {
		extractor.extractTo(table);
	}
	
	public String generateSimpleReport() {
		if (table.isEmpty())
			return "No data to generate report";
		
		List<Report> reports = new ArrayList<Report>();
		
		for (BuildHealthAnalyser analyser : analysers)
			reports.addAll(analyser.computeSimpleReport(table));
		
		if (reports.isEmpty())
			return "No data to generate report";
		
		return new ReportFormater().format(reports);
	}
	
}
