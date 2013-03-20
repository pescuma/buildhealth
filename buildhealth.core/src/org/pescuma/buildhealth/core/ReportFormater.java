package org.pescuma.buildhealth.core;

import java.util.Arrays;
import java.util.List;

public class ReportFormater {
	
	private static final String NO_DATA = "No data to generate report";
	private static final String PREFIX = "    ";
	
	public String format(Report report) {
		if (report == null)
			return NO_DATA;
		
		StringBuilder result = new StringBuilder();
		
		if (report.getName().equals("Build")) {
			result.append(createSummaryLine(report)).append("\n");
			append(result, report.getChildren(), PREFIX);
			
		} else {
			append(result, Arrays.asList(report), "");
		}
		
		return result.toString();
	}
	
	public String createSummaryLine(Report report) {
		if (report == null)
			return NO_DATA;
		
		return "Your build is " + createTitle(report.getStatus());
	}
	
	private void append(StringBuilder result, List<Report> reports, String prefix) {
		for (Report report : reports) {
			result.append(prefix);
			result.append(report.getName()).append(": ").append(report.getValue());
			String description = report.getDescription();
			if (!description.isEmpty())
				result.append(" [").append(description).append("]");
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
