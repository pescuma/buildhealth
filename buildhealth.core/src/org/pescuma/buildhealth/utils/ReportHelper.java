package org.pescuma.buildhealth.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.pescuma.buildhealth.core.BuildHealth.ReportFlags;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

public class ReportHelper {
	
	/**
	 * The list remains intact, but the reports inside it are changed to respect the options
	 */
	public static void simplifyReport(List<Report> reports, int opts) {
		for (Report report : reports)
			simplifyReport(report, opts);
	}
	
	/**
	 * Change the children of the report to respect the opts
	 */
	private static void simplifyReport(Report report, int opts) {
		boolean highlighProblems = (opts & ReportFlags.HighlightProblems) != 0;
		boolean summaryOnly = (opts & ReportFlags.SummaryOnly) != 0;
		
		if (summaryOnly && !highlighProblems) {
			report.getChildren().clear();
			return;
		}
		
		if (summaryOnly)
			removeGoodChildren(report);
		
		if (highlighProblems)
			sort(report);
	}
	
	private static void removeGoodChildren(Report report) {
		report.visit(new Report.Visitor() {
			@Override
			public void posVisit(Report report) {
				for (Iterator<Report> it = report.getChildren().iterator(); it.hasNext();) {
					Report child = it.next();
					if (child.getStatus() == BuildStatus.Good && child.getChildren().isEmpty())
						it.remove();
				}
			}
		});
	}
	
	private static void sort(Report report) {
		final Comparator<Report> comp = new Comparator<Report>() {
			@Override
			public int compare(Report o1, Report o2) {
				return precedence(o1.getStatus()) - precedence(o2.getStatus());
			}
			
			private int precedence(BuildStatus status) {
				switch (status) {
					case Good:
						return 2;
					case SoSo:
						return 1;
					case Problematic:
						return 0;
					default:
						throw new IllegalStateException();
				}
			}
		};
		
		report.visit(new Report.Visitor() {
			@Override
			public void preVisit(Report report) {
				Collections.sort(report.getChildren(), comp);
			}
		});
	}
	
	public static List<Report> findSourcesOfProblems(List<Report> reports) {
		final List<Report> result = new ArrayList<Report>();
		
		for (Report report : reports) {
			report.visit(new Report.Visitor() {
				@Override
				public void preVisit(Report report) {
					if (report.isSourceOfProblem())
						result.add(report);
				}
			});
		}
		
		return result;
	}
}
