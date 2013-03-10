package org.pescuma.buildhealth.core;

import java.util.List;

public class ReportFormater {
	
	public String format(List<Report> reports) {
		StringBuilder result = new StringBuilder();
		
		result.append(createTitle(mergeBuildStatus(reports))).append("\n");
		
		for (Report report : reports) {
			result.append(report.getName()).append(": ").append(report.getValue());
			result.append(" [").append(report.getDescription()).append("]");
			result.append("\n");
		}
		
		return result.toString();
	}
	
	private BuildStatus mergeBuildStatus(List<Report> reports) {
		BuildStatus status = null;
		for (Report report : reports)
			status = BuildStatus.merge(status, report.getStatus());
		return status;
	}
	
	private String createTitle(BuildStatus status) {
		switch (status) {
			case Successful:
				return "Build was SUCCESSFUL";
			case Failed:
				return "Build FAILED";
			case SoSo:
				return "Build is So So";
			default:
				throw new IllegalStateException();
		}
	}
	
}
