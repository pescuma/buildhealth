package org.pescuma.buildhealth.core;

import java.util.List;

public class ReportFormater {
	
	private static final String NO_DATA = "No data to generate report";
	private static final String PREFIX = "    ";
	
	private boolean showDescriptions = true;
	
	public ReportFormater hideDescriptions() {
		showDescriptions = false;
		return this;
	}
	
	public String createSummaryLine(Report report) {
		StringOutputer out = new StringOutputer();
		createSummaryLine(report, out);
		return out.toString();
	}
	
	public void createSummaryLine(Report report, Outputer out) {
		if (report == null) {
			out.append(NO_DATA);
		} else {
			appendSummaryLine(report, out);
		}
	}
	
	private void appendSummaryLine(Report report, Outputer out) {
		if (report.getName().equals("Build"))
			out.append("Your build is ").append(createTitle(report.getStatus()), report.getStatus());
		else
			out.append(report.getName()).append(": ").append(report.getValue(), report.getStatus());
		
		String description = report.getDescription();
		if (showDescriptions && !description.isEmpty())
			out.append(" [").append(description).append("]");
	}
	
	public String format(Report report) {
		StringOutputer out = new StringOutputer();
		format(report, out);
		return out.toString();
	}
	
	public void format(Report report, Outputer out) {
		if (report == null) {
			out.append(NO_DATA);
			out.append("\n");
			
		} else {
			appendSummaryLine(report, out);
			out.append("\n");
			
			append(PREFIX, report.getChildren(), out);
		}
	}
	
	private void append(String prefix, List<Report> reports, Outputer out) {
		for (Report report : reports) {
			out.append(prefix);
			appendSummaryLine(report, out);
			out.append("\n");
			
			append(prefix + PREFIX, report.getChildren(), out);
		}
	}
	
	public static String createTitle(BuildStatus status) {
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
	
	public static interface Outputer {
		Outputer append(String text);
		
		Outputer append(String text, BuildStatus status);
	}
	
	public static class StringOutputer implements Outputer {
		private final StringBuilder result = new StringBuilder();
		
		@Override
		public Outputer append(String text) {
			result.append(text);
			return this;
		}
		
		@Override
		public Outputer append(String text, BuildStatus status) {
			result.append(text);
			return this;
		}
		
		@Override
		public String toString() {
			return result.toString();
		}
	}
	
}
