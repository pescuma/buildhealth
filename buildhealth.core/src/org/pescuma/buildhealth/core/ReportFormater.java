package org.pescuma.buildhealth.core;

import java.util.List;

public class ReportFormater {
	
	private static final String NO_DATA = "No data to generate report";
	private static final String PREFIX = "    ";
	
	private final Outputer outputer;
	
	public ReportFormater() {
		this(new Outputer() {
			private StringBuilder result = new StringBuilder();
			
			@Override
			public Outputer start() {
				result = new StringBuilder();
				return this;
			}
			
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
		});
	}
	
	public ReportFormater(Outputer outputer) {
		this.outputer = outputer;
	}
	
	public String createSummaryLine(Report report) {
		if (report == null)
			return NO_DATA;
		
		outputer.start();
		appendSummaryLine(report);
		return outputer.toString();
	}
	
	private void appendSummaryLine(Report report) {
		if (report.getName().equals("Build"))
			outputer.append("Your build is ").append(createTitle(report.getStatus()), report.getStatus());
		else
			outputer.append(report.getName()).append(": ").append(report.getValue(), report.getStatus());
		
		String description = report.getDescription();
		if (!description.isEmpty())
			outputer.append(" [").append(description).append("]");
	}
	
	public String format(Report report) {
		if (report == null)
			return NO_DATA;
		
		outputer.start();
		
		appendSummaryLine(report);
		outputer.append("\n");
		
		append(report.getChildren(), PREFIX);
		
		return outputer.toString();
	}
	
	private void append(List<Report> reports, String prefix) {
		for (Report report : reports) {
			outputer.append(prefix);
			appendSummaryLine(report);
			outputer.append("\n");
			
			append(report.getChildren(), prefix + PREFIX);
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
	
	public static interface Outputer {
		Outputer start();
		
		Outputer append(String text);
		
		Outputer append(String text, BuildStatus status);
		
		@Override
		String toString();
	}
	
}
