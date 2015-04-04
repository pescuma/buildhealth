package org.pescuma.buildhealth.analyser.unittest;

import static java.lang.Math.*;
import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.utils.NumbersFormater.*;
import static org.pescuma.buildhealth.core.BuildHealth.ReportFlags.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.utils.SimpleTree;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.projects.Projects;
import org.pescuma.buildhealth.utils.Location;
import org.pescuma.datatable.DataTable;
import org.pescuma.datatable.DataTable.Line;
import org.pescuma.datatable.DataTable.Value;

import com.google.common.base.Function;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Unit test,language,framework,{passed,error,failed,skipped,time},test suite name,method name,location,message,stack trace
 * </pre>
 * 
 * For time the value is in seconds, for the others is the number of tests.
 * 
 * Location can be one of:
 * <ul>
 * <li>filename
 * <li>filename>line
 * <li>filename>line:column
 * <li>filename>beginLine:beginColumn:endLine:endColumn
 * <li>or multiple of above, separated by | (ex: file1|file2:32)
 * </ul>
 * 
 * Example:
 * 
 * <pre>
 * 10 | Unit test,Java,JUnit,passed,package.TestWithoutMethodsInfo
 * 1 | Unit test,Java,JUnit,passed,package.TestWithMethodInfo,testMethod1
 * 1 | Unit test,Java,JUnit,failed,package.TestWithMethodInfo,testMethod2
 * 1 | Unit test,Java,JUnit,passed,package.TestWithMethodInfo,testMethod3,/a/b.java>12:1:12:5
 * 1 | Unit test,Java,JUnit,error,package.TestWithMethodInfo,testMethod4
 * 1 | Unit test,Java,JUnit,passed,package.TestWithMethodAndTimeInfo,testMethod1
 * 0.01 | Unit test,Java,JUnit,time,package.TestWithMethodAndTimeInfo,testMethod1
 * </pre>
 */
@MetaInfServices
public class UnitTestAnalyser implements BuildHealthAnalyser {
	
	public static final int COLUMN_LANGUAGE = 1;
	public static final int COLUMN_FRAMEWORK = 2;
	public static final int COLUMN_TYPE = 3;
	public static final int COLUMN_SUITE = 4;
	public static final int COLUMN_TEST = 5;
	public static final int COLUMN_LOCATION = 6;
	public static final int COLUMN_MESSAGE = 7;
	public static final int COLUMN_STACK = 8;
	
	public static final String TYPE_ERROR = "error";
	public static final String TYPE_FAILED = "failed";
	public static final String TYPE_PASSED = "passed";
	public static final String TYPE_TIME = "time";
	
	@Override
	public String getName() {
		return "Unit tests";
	}
	
	@Override
	public int getPriority() {
		return 100;
	}
	
	@Override
	public List<BuildHealthPreference> getKnownPreferences() {
		return Collections.emptyList();
	}
	
	@Override
	public List<Report> computeReport(DataTable data, Projects projects, Preferences prefs, int opts) {
		data = data.filter("Unit test");
		if (data.isEmpty())
			return Collections.emptyList();
		
		boolean highlighProblems = (opts & HighlightProblems) != 0 && (opts & SummaryOnly) == 0 && hasProblems(data);
		
		SimpleTree<Stats> tree = toTree(data, projects);
		
		if (highlighProblems)
			tree = splitTree(tree);
		
		computeParentStats(tree);
		
		return asList((Report) toReport(tree.getRoot(), getName()));
	}
	
	private boolean hasProblems(DataTable data) {
		return !data.filter(COLUMN_TYPE, TYPE_FAILED).isEmpty() || !data.filter(COLUMN_TYPE, TYPE_ERROR).isEmpty();
	}
	
	private SimpleTree<Stats> toTree(DataTable data, Projects projects) {
		SimpleTree<Stats> tree = newTree();
		
		for (Line line : data.getLines()) {
			SimpleTree<Stats>.Node node = tree.getRoot();
			node = node.getChild(line.getColumn(COLUMN_LANGUAGE));
			node = node.getChild(line.getColumn(COLUMN_FRAMEWORK));
			
			List<Location> locations = Location.parse(line.getColumn(COLUMN_LOCATION));
			String project = projects.findProjectForLocations(locations);
			if (project != null)
				node = node.getChild(project);
			
			String suiteName = line.getColumn(COLUMN_SUITE);
			if (suiteName.isEmpty())
				suiteName = "No suite name";
			
			node = node.getChild(suiteName);
			
			String testName = line.getColumn(COLUMN_TEST);
			
			if (!testName.isEmpty())
				node = node.getChild(testName);
			
			Stats stats = node.getData();
			stats.isFromData = true;
			stats.isTest = !testName.isEmpty();
			
			stats.add(line.getColumn(COLUMN_TYPE), line.getValue());
			
			String message = line.getColumn(COLUMN_MESSAGE);
			String stack = line.getColumn(COLUMN_STACK);
			if (!message.isEmpty() || !stack.isEmpty()) {
				if (stats.message.length() > 0)
					stats.message.append("\n");
				if (!message.isEmpty())
					stats.message.append(message).append("\n");
				if (!stack.isEmpty())
					stats.message.append(stack).append("\n");
			}
		}
		
		tree.visit(new SimpleTree.Visitor<Stats>() {
			@Override
			public void preVisitNode(SimpleTree<Stats>.Node node) {
				Stats stats = node.getData();
				if (!stats.isTest)
					return;
				
				Double time = stats.getTime();
				if (time == null)
					return;
				
				if (stats.message.length() > 0)
					stats.message.append("\n");
				stats.message.append("Executed in ").append(format1000(time, "s"));
			}
		});
		
		return tree;
	}
	
	private SimpleTree<Stats> newTree() {
		return new SimpleTree<Stats>(new Function<String[], Stats>() {
			@Override
			public Stats apply(String[] name) {
				return new Stats(name);
			}
		});
	}
	
	private SimpleTree<Stats> splitTree(SimpleTree<Stats> tree) {
		final SimpleTree<Stats> result = newTree();
		
		tree.visit(new SimpleTree.Visitor<Stats>() {
			@Override
			public void preVisitNode(SimpleTree<Stats>.Node node) {
				Stats stats = node.getData();
				
				if (!stats.isFromData)
					return;
				
				int total = stats.getTotal();
				int errors = stats.getErrors();
				int failures = stats.getFailures();
				int problematic = errors + failures;
				
				if (problematic > 0 && total != problematic) {
					// Split
					
					Stats passed = result.getNode("Passed").getChild(stats.name).getData();
					passed.set(stats);
					passed.types.remove(TYPE_ERROR);
					passed.types.remove(TYPE_FAILED);
					
					Stats failed = result.getNode("Failed").getChild(stats.name).getData();
					failed.add(TYPE_ERROR, errors);
					failed.add(TYPE_FAILED, failures);
					
					// Messages will get duplicated
					failed.message.append(stats.message);
					
					Double time = stats.getTime();
					if (time != null) {
						double passedTime = time * (total - problematic) / total;
						passed.types.get(TYPE_TIME).value = passedTime;
						failed.add(TYPE_TIME, time - passedTime);
					}
					
				} else {
					result.getNode(problematic > 0 ? "Failed" : "Passed").getChild(stats.name).getData().set(stats);
				}
			}
		});
		
		return result;
	}
	
	private void computeParentStats(SimpleTree<Stats> tree) {
		tree.visit(new SimpleTree.Visitor<Stats>() {
			Deque<Stats> stack = new ArrayDeque<Stats>();
			
			@Override
			public void preVisitNode(SimpleTree<Stats>.Node node) {
				stack.push(node.getData());
			}
			
			@Override
			public void posVisitNode(SimpleTree<Stats>.Node node) {
				stack.pop();
				
				if (stack.isEmpty())
					return;
				
				Stats stats = node.getData();
				Stats parent = stack.peek();
				if (!parent.isFromData)
					parent.add(stats);
			}
		});
	}
	
	private UnitTestReport toReport(SimpleTree<Stats>.Node node, String name) {
		List<UnitTestReport> children = new ArrayList<UnitTestReport>();
		
		for (SimpleTree<Stats>.Node child : node.getChildren())
			children.add(toReport(child, child.getName()));
		
		Stats stats = node.getData();
		
		return new UnitTestReport(stats.getStatus(), name, stats.getPassed(), stats.getErrors(), stats.getFailures(),
				stats.getTime(), stats.computeDescription(), stats.getProblemDescription(), children);
	}
	
	private static class Stats {
		
		final String[] name;
		final Map<String, Value> types = new HashMap<String, Value>();
		final StringBuilder message = new StringBuilder();
		boolean isFromData;
		boolean isTest;
		
		Stats(String[] name) {
			this.name = name;
		}
		
		void add(Stats other) {
			for (Map.Entry<String, Value> entry : other.types.entrySet())
				add(entry.getKey(), entry.getValue().value);
		}
		
		void add(String name, double value) {
			Value v = types.get(name);
			if (v == null) {
				v = new Value();
				types.put(name, v);
			}
			v.value += value;
		}
		
		BuildStatus getStatus() {
			return getErrors() + getFailures() > 0 ? BuildStatus.Problematic : BuildStatus.Good;
		}
		
		public String getProblemDescription() {
			if (!isFromData)
				return null;
			
			if (getFailures() > 0)
				return "Unit test failed";
			else if (getErrors() > 0)
				return "Unit test with error";
			else
				return null;
		}
		
		int getInt(String type) {
			Value v = types.get(type);
			if (v == null)
				return 0;
			else
				return (int) round(v.value);
		}
		
		int getTotal() {
			double total = 0;
			for (Map.Entry<String, Value> entry : types.entrySet()) {
				if (!entry.getKey().equals(UnitTestAnalyser.TYPE_TIME))
					total += entry.getValue().value;
			}
			return (int) round(total);
		}
		
		int getErrors() {
			return getInt(UnitTestAnalyser.TYPE_ERROR);
		}
		
		int getFailures() {
			return getInt(UnitTestAnalyser.TYPE_FAILED);
		}
		
		int getPassed() {
			return getInt(UnitTestAnalyser.TYPE_PASSED);
		}
		
		Double getTime() {
			Value v = types.get(UnitTestAnalyser.TYPE_TIME);
			if (v == null)
				return null;
			else
				return v.value;
		}
		
		String computeDescription() {
			if (isTest)
				return message.toString();
			
			StringBuilder result = new StringBuilder();
			
			append(result, getTotal(), "test", "tests");
			append(result, getPassed(), "passed");
			append(result, getErrors(), "error", "errors");
			append(result, getFailures(), "failure", "failures");
			for (Map.Entry<String, Value> entry : getOtherTypes().entrySet())
				append(result, (int) round(entry.getValue().value), entry.getKey());
			
			Double time = getTime();
			if (time != null)
				result.append(" (").append(format1000(time, "s")).append(")");
			
			if (message.length() > 0)
				result.append("\n\n").append(message);
			
			return result.toString();
		}
		
		private Map<String, Value> getOtherTypes() {
			Map<String, Value> result = new TreeMap<String, Value>(types);
			result.remove(UnitTestAnalyser.TYPE_ERROR);
			result.remove(UnitTestAnalyser.TYPE_FAILED);
			result.remove(UnitTestAnalyser.TYPE_PASSED);
			result.remove(UnitTestAnalyser.TYPE_TIME);
			return result;
		}
		
		private static void append(StringBuilder out, int count, String name) {
			append(out, count, name, name);
		}
		
		private static void append(StringBuilder out, int count, String name, String namePlural) {
			if (count <= 0)
				return;
			if (out.length() > 0)
				out.append(", ");
			out.append(count).append(" ").append(count == 1 ? name : namePlural);
		}
		
		void set(Stats other) {
			isFromData = other.isFromData;
			isTest = other.isTest;
			
			types.clear();
			types.putAll(other.types);
			
			message.setLength(0);
			message.append(other.message);
		}
	}
}
