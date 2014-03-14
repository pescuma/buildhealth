package org.pescuma.buildhealth.utils;

import java.util.List;

import org.pescuma.buildhealth.core.BuildReport;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

public class ReportFormater {
	
	public static final String NO_DATA = "No data to generate report";
	private static final String PREFIX = "    ";
	
	private boolean showDescriptions = true;
	private boolean writeStatuses = false;
	
	public ReportFormater hideDescriptions() {
		showDescriptions = false;
		return this;
	}
	
	public ReportFormater writeBuildStatuses() {
		writeStatuses = true;
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
			appendSummaryLine(report, showDescriptions, out);
		}
	}
	
	private void appendSummaryLine(Report report, boolean withDescription, Outputer out) {
		if (report instanceof BuildReport) {
			out.append("Your build is ").append(createTitle(report.getStatus()), report.getStatus());
			
		} else {
			out.append(report.getName());
			
			if (!report.getValue().isEmpty())
				out.append(": ").append(report.getValue(), report.getStatus());
			
			if (writeStatuses && report.getStatus() != BuildStatus.Good)
				out.append(" (").append(createTitle(report.getStatus()), report.getStatus()).append(")");
		}
		
		String description = report.getDescription();
		if (withDescription && !description.isEmpty())
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
			appendSummaryLine(report, showDescriptions, out);
			out.append("\n");
			
			append(PREFIX, report.getChildren(), out);
			
			appendSourcesOfProblems(report, out);
		}
	}
	
	private void appendSourcesOfProblems(Report report, Outputer out) {
		if (!(report instanceof BuildReport))
			return;
		
		List<Report> sourcesOfProblems = ((BuildReport) report).getSourcesOfProblems();
		if (sourcesOfProblems == null || sourcesOfProblems.isEmpty())
			return;
		
		out.append("\n");
		if (report.getStatus() == BuildStatus.Problematic)
			out.append("Sources of problems:\n");
		else
			out.append("Sources of instability:\n");
		
		for (Report source : sourcesOfProblems) {
			out.append(PREFIX);
			out.append(source.getProblemDescription()).append(" [");
			appendSummaryLine(source, false, out);
			out.append("]");
			out.append("\n");
		}
	}
	
	private void append(String prefix, List<Report> reports, Outputer out) {
		for (Report report : reports) {
			out.append(prefix);
			appendSummaryLine(report, showDescriptions, out);
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
