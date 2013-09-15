package org.pescuma.buildhealth.analyser.performance;

import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.BuildStatusHelper.*;
import static org.pescuma.buildhealth.analyser.NumbersFormater.*;
import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.utils.SimpleTree;
import org.pescuma.buildhealth.utils.SimpleTree.Visitor;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Performance,language,framework,{type:ms,runsPerS},test
 * </pre>
 * 
 * Example:
 * 
 * <pre>
 * 10 | Performance,Java,Japex,ms,SerializaionA.small
 * 15 | Performance,Java,Japex,ms,SerializaionA.big
 * 7 | Performance,Java,Japex,ms,SerializaionB.small
 * 12 | Performance,Java,Japex,ms,SerializaionA.big
 * 10 | Performance,Java,Japex,runsPerS,Timers/Serializaion.big
 * </pre>
 */
@MetaInfServices
public class PerformanceAnalyser implements BuildHealthAnalyser {
	
	public static final int COLUMN_LANGUAGE = 1;
	public static final int COLUMN_FRAMEWORK = 2;
	public static final int COLUMN_TYPE = 3;
	public static final int COLUMN_TEST = 4;
	
	public static final String TYPE_MS = "ms";
	public static final String TYPE_RUNS_PER_S = "runsPerS";
	
	@Override
	public String getName() {
		return "Performance";
	}
	
	@Override
	public int getPriority() {
		return 500;
	}
	
	@Override
	public List<BuildHealthPreference> getPreferences() {
		List<BuildHealthPreference> result = new ArrayList<BuildHealthPreference>();
		
		result.add(new BuildHealthPreference("Minimun runs per second for a Good build", "<no limit>", "performace",
				TYPE_RUNS_PER_S, "good"));
		result.add(new BuildHealthPreference("Minimun runs per second for a Good build", "<no limit>", "performace",
				TYPE_RUNS_PER_S, "warn"));
		
		result.add(new BuildHealthPreference("Maximun run time (ms) for a Good build", "<no limit>", "performace",
				TYPE_MS, "good"));
		result.add(new BuildHealthPreference("Maximun run time (ms) for a Good build", "<no limit>", "performace",
				TYPE_MS, "warn"));
		
		result.add(new BuildHealthPreference("How to show the agregated results (runsPerS or ms)",
				"<ms if both available, else what is available>", "performace", "report"));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public List<Report> computeReport(BuildData data, Preferences prefs, int opts) {
		data = data.filter("Performance");
		if (data.isEmpty())
			return Collections.emptyList();
		
		prefs = prefs.child("performance");
		
		boolean highlighProblems = (opts & HighlightProblems) != 0;
		boolean summaryOnly = (opts & SummaryOnly) != 0;
		
		SimpleTree<Stats> tree = buildTree(data);
		
		sumChildStatsAndComputeStatus(tree, prefs);
		
		if (summaryOnly)
			stripToSummary(tree, highlighProblems);
		
		return asList(toReport(tree.getRoot(), getName(), prefs, highlighProblems));
	}
	
	private SimpleTree<Stats> buildTree(BuildData data) {
		SimpleTree<Stats> tree = new SimpleTree<Stats>(new Function<String[], Stats>() {
			@Override
			public Stats apply(String[] name) {
				return new Stats(name);
			}
		});
		
		for (Line line : data.getLines()) {
			String type = line.getColumn(COLUMN_TYPE);
			
			if (!TYPE_MS.equals(type) && !TYPE_RUNS_PER_S.equals(type))
				continue;
			
			SimpleTree<Stats>.Node node = tree.getRoot();
			
			node = node.getChild(line.getColumn(COLUMN_LANGUAGE));
			node = node.getChild(line.getColumn(COLUMN_FRAMEWORK));
			
			String test = line.getColumn(COLUMN_TEST);
			for (String piece : splitCategory(test))
				node = node.getChild(piece);
			
			Stats stats = node.getData();
			stats.originalNames.add(test);
			
			if (TYPE_MS.equals(type))
				stats.addMs(line.getValue());
			else
				stats.addRunsPerS(line.getValue());
		}
		
		return tree;
	}
	
	private void sumChildStatsAndComputeStatus(SimpleTree<Stats> tree, final Preferences prefs) {
		tree.visit(new SimpleTree.Visitor<Stats>() {
			Deque<Stats> parents = new ArrayDeque<Stats>();
			
			@Override
			public void preVisitNode(SimpleTree<Stats>.Node node) {
				parents.push(node.getData());
			}
			
			@Override
			public void posVisitNode(SimpleTree<Stats>.Node node) {
				parents.pop();
				
				Stats stats = node.getData();
				
				stats.computeStatus(prefs);
				
				if (!parents.isEmpty())
					parents.peekFirst().add(stats);
			}
		});
	}
	
	private void stripToSummary(SimpleTree<Stats> tree, boolean highlighProblems) {
		if (!highlighProblems)
			tree.getRoot().removeChildIf(new Predicate<SimpleTree<Stats>.Node>() {
				@Override
				public boolean apply(SimpleTree<Stats>.Node input) {
					return true;
				}
			});
		
		else
			tree.visit(new Visitor<Stats>() {
				@Override
				public void preVisitNode(SimpleTree<Stats>.Node node) {
					node.removeChildIf(new Predicate<SimpleTree<Stats>.Node>() {
						@Override
						public boolean apply(SimpleTree<Stats>.Node input) {
							return input.getData().statusWithChildren == BuildStatus.Good;
						}
					});
				}
			});
	}
	
	private Report toReport(SimpleTree<Stats>.Node node, String name, Preferences prefs, boolean highlighProblems) {
		List<Report> children = new ArrayList<Report>();
		
		for (SimpleTree<Stats>.Node child : sort(node.getChildren(), highlighProblems))
			children.add(toReport(child, child.getName(), prefs, highlighProblems));
		
		Stats stats = node.getData();
		
		return new Report(node.isRoot() ? stats.statusWithChildren : stats.status, name, stats.toText(prefs), children);
	}
	
	private Collection<SimpleTree<Stats>.Node> sort(Collection<SimpleTree<Stats>.Node> nodes, boolean highlighProblems) {
		if (!highlighProblems)
			return nodes;
		
		List<SimpleTree<Stats>.Node> result = new ArrayList<SimpleTree<Stats>.Node>(nodes);
		
		Collections.sort(result, new Comparator<SimpleTree<Stats>.Node>() {
			@Override
			public int compare(SimpleTree<Stats>.Node o1, SimpleTree<Stats>.Node o2) {
				int cmp = precedence(o1.getData().statusWithChildren) - precedence(o2.getData().statusWithChildren);
				if (cmp != 0)
					return cmp;
				
				return o1.getName().compareToIgnoreCase(o2.getName());
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
		});
		
		return result;
	}
	
	private static class Stats {
		final String[] name;
		final Set<String> originalNames = new HashSet<String>();
		int msCount = 0;
		double msTotal = 0;
		double runsPerSTotal = 0;
		BuildStatus status;
		BuildStatus statusWithChildren = BuildStatus.Good;
		
		Stats(String[] name) {
			this.name = name;
		}
		
		void addMs(double value) {
			msCount++;
			msTotal += value;
			runsPerSTotal += 1000 / value;
		}
		
		void addRunsPerS(double value) {
			msTotal += 1000 / value;
			runsPerSTotal += value;
		}
		
		void add(Stats stats) {
			msCount += stats.msCount;
			msTotal += stats.msTotal;
			runsPerSTotal += stats.runsPerSTotal;
			statusWithChildren = statusWithChildren.mergeWith(stats.statusWithChildren);
		}
		
		void computeStatus(Preferences prefs) {
			status = statusFromThreshold(prefs, TYPE_RUNS_PER_S, runsPerSTotal, true) //
					.mergeWith(statusFromThreshold(prefs, TYPE_MS, msTotal, false));
			
			statusWithChildren = statusWithChildren.mergeWith(status);
		}
		
		private BuildStatus statusFromThreshold(Preferences pref, String type, double total, boolean biggerIsBetter) {
			BuildStatus result;
			if (name.length < 1)
				result = computeStatusFromThreshold(pref.child(type), total, biggerIsBetter);
			else
				result = computeStatusFromThreshold(pref.child(name).child(type), total, biggerIsBetter);
			
			for (String originalName : originalNames)
				if (!originalName.isEmpty())
					result = result.mergeWith(computeStatusFromThreshold(pref.child(originalName).child(type), total,
							biggerIsBetter));
			
			return result;
		}
		
		String toText(Preferences prefs) {
			boolean reportMs = isToReportMs(prefs, msCount);
			
			if (reportMs)
				return format1000(msTotal / 1000, "s");
			else
				return format1000(runsPerSTotal, "") + " runs per second";
		}
		
		private boolean isToReportMs(Preferences prefs, int msTotal) {
			String reportType = prefs.get("report", "");
			
			if (reportType.equals(TYPE_MS))
				return true;
			
			else if (reportType.equals(TYPE_RUNS_PER_S))
				return false;
			
			else
				return msTotal > 0;
		}
	}
	
}
