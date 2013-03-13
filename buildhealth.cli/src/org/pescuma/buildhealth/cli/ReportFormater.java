package org.pescuma.buildhealth.cli;

import java.util.Arrays;
import java.util.List;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

public class ReportFormater {
	
	private static final String PREFIX = "    ";
	
	public String format(Report report) {
		if (report == null)
			return "No data to generate report";
		
		StringBuilder result = new StringBuilder();
		
		if (report.getName().equals("Build")) {
			result.append("Your build is ").append(createTitle(report.getStatus())).append("\n");
			append(result, report.getChildren(), PREFIX);
			
		} else {
			append(result, Arrays.asList(report), "");
		}
		
		return result.toString();
	}
	
	private void append(StringBuilder result, List<Report> reports, String prefix) {
		for (Report report : reports) {
			result.append(prefix);
			result.append(report.getName()).append(": ").append(report.getValue());
			result.append(" [").append(report.getDescription()).append("]");
			result.append("\n");
			
			append(result, report.getChildren(), prefix + PREFIX);
		}
	}
	
	private String createTitle(BuildStatus status) {
		switch (status) {
			case Good:
				return "GOOD";
			case Problematic:
				return "PROBLEMATIC";
			case SoSo:
				return "So So";
			default:
				throw new IllegalStateException();
		}
	}
	
}
