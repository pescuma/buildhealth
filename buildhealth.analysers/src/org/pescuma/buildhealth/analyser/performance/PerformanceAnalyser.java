package org.pescuma.buildhealth.analyser.performance;

import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.utils.NumbersFormater.*;
import static org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusMessageFormaterHelper.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.utils.BuildHealthAnalyserUtils.TreeStats;
import org.pescuma.buildhealth.analyser.utils.SimpleTree;
import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusAndExplanation;
import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusFromThresholdComputer;
import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusMessageFormater;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;

import com.google.common.base.Function;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Performance,language,framework,{type:ms,runsPerS},test,test,test,...
 * </pre>
 * 
 * Example:
 * 
 * <pre>
 * 10 | Performance,Java,Japex,ms,SerializaionA,small
 * 15 | Performance,Java,Japex,ms,SerializaionA,big
 * 7 | Performance,Java,Japex,ms,SerializaionB,small
 * 12 | Performance,Java,Japex,ms,SerializaionA,big
 * 10 | Performance,Java,Japex,runsPerS,Timers,Serializaion,big
 * </pre>
 */
@MetaInfServices
public class PerformanceAnalyser implements BuildHealthAnalyser {
	
	public static final int COLUMN_LANGUAGE = 1;
	public static final int COLUMN_FRAMEWORK = 2;
	public static final int COLUMN_TYPE = 3;
	public static final int COLUMN_TEST_START = 4;
	
	public static final String TYPE_MS = "ms";
	public static final String TYPE_RUNS_PER_S = "runsPerS";
	
	private static final BuildStatusFromThresholdComputer runsPerSStatusComputer = new BuildStatusFromThresholdComputer(
			true, new BuildStatusMessageFormater() {
				@Override
				public String computeSoSoMessage(double good, String[] prefKey) {
					return "Instable if has less than " + formatRunsPerS(good) + prefKeyToMessage(removeLast(prefKey));
				}
				
				@Override
				public String computeProblematicMessage(double warn, String[] prefKey) {
					return "Should not have less than " + formatRunsPerS(warn) + prefKeyToMessage(removeLast(prefKey));
				}
				
			});
	
	private static final BuildStatusFromThresholdComputer msStatusComputer = new BuildStatusFromThresholdComputer(
			false, new BuildStatusMessageFormater() {
				@Override
				public String computeSoSoMessage(double good, String[] prefKey) {
					return "Instable if takes more than " + formatMs(good) + " to run"
							+ prefKeyToMessage(removeLast(prefKey));
				}
				
				@Override
				public String computeProblematicMessage(double warn, String[] prefKey) {
					return "Should not take more than " + formatMs(warn) + " to run"
							+ prefKeyToMessage(removeLast(prefKey));
				}
			});
	
	private static List<String> removeLast(String[] prefKey) {
		return asList(ArrayUtils.remove(prefKey, prefKey.length - 1));
	}
	
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
		
		SimpleTree<Stats> tree = buildTree(data);
		
		sumChildStatsAndComputeBuildStatuses(tree, prefs);
		
		return asList(toReport(tree.getRoot(), getName(), prefs));
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
			
			String[] columns = line.getColumns();
			for (int i = COLUMN_TEST_START; i < columns.length; i++)
				node = node.getChild(columns[i]);
			
			Stats stats = node.getData();
			
			if (TYPE_MS.equals(type))
				stats.addMs(line.getValue());
			else
				stats.addRunsPerS(line.getValue());
		}
		
		return tree;
	}
	
	private void sumChildStatsAndComputeBuildStatuses(SimpleTree<Stats> tree, final Preferences prefs) {
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
	
	private Report toReport(SimpleTree<Stats>.Node node, String name, Preferences prefs) {
		List<Report> children = new ArrayList<Report>();
		
		for (SimpleTree<Stats>.Node child : node.getChildren())
			children.add(toReport(child, child.getName(), prefs));
		
		Stats stats = node.getData();
		
		return new Report(node.isRoot() ? stats.getStatusWithChildren() : stats.getStatus(), name, stats.toText(prefs),
				null, stats.getProblemDescription(), children);
	}
	
	private static class Stats extends TreeStats {
		
		int msCount = 0;
		double msTotal = 0;
		double runsPerSTotal = 0;
		
		Stats(String[] name) {
			super(name);
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
			
			mergeChildStatus(stats);
		}
		
		void computeStatus(Preferences prefs) {
			String[] keyStart = getNames();
			
			BuildStatusAndExplanation status = BuildStatusAndExplanation.merge(
					runsPerSStatusComputer.compute(runsPerSTotal, prefs, keyStart, TYPE_RUNS_PER_S),
					msStatusComputer.compute(msTotal, prefs, keyStart, TYPE_MS));
			
			if (status != null)
				setOwnStatus(status);
		}
		
		String toText(Preferences prefs) {
			boolean reportMs = isToReportMs(prefs, msCount);
			
			if (reportMs)
				return formatMs(msTotal);
			else
				return formatRunsPerS(runsPerSTotal);
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
	
	private static String formatMs(double v) {
		return format1000(v / 1000, "s");
	}
	
	private static String formatRunsPerS(double v) {
		return format1000(v, "") + " runs per second";
	}
	
}
