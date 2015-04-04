package org.pescuma.buildhealth.analyser.compilererrors;

import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.utils.NumbersFormater.*;
import static org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusMessageFormaterHelper.*;
import static org.pescuma.buildhealth.core.prefs.BuildHealthPreference.*;
import static org.pescuma.buildhealth.utils.StringHelper.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.utils.BuildHealthAnalyserUtils.TreeStats;
import org.pescuma.buildhealth.analyser.utils.SimpleTree;
import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusAndExplanation;
import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusFromThresholdComputerConsideringParents;
import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusMessageFormater;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.projects.Projects;
import org.pescuma.buildhealth.utils.Location;
import org.pescuma.datatable.DataTable;
import org.pescuma.datatable.DataTable.Line;

import com.google.common.base.Function;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Compiler error,language,framework,location,category,message,details
 * </pre>
 * 
 * The number is the number of compiler errors in that place.
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
 * To be able to use the details (anything after filename) at least the filename is required.
 * 
 * Example:
 * 
 * <pre>
 * 1 | Compiler error,Java,javac,/a/b.java:12,,Variable not defined
 * 1 | Compiler error,Java,javac,/a/b.java:12,Not defined/Methods,Method not defined
 * 10 | Compiler error,Java,javac,/a/c.java,,Type2
 * </pre>
 */
@MetaInfServices
public class CompilerErrorsAnalyser implements BuildHealthAnalyser {
	
	public static final int COLUMN_LANGUAGE = 1;
	public static final int COLUMN_FRAMEWORK = 2;
	public static final int COLUMN_LOCATION = 3;
	public static final int COLUMN_CATEGORY = 4;
	public static final int COLUMN_MESSAGE = 5;
	public static final int COLUMN_DETAILS = 6;
	
	private final BuildStatusFromThresholdComputerConsideringParents statusComputer = new BuildStatusFromThresholdComputerConsideringParents(
			new BuildStatusMessageFormater() {
				@Override
				public String computeSoSoMessage(double good, String[] prefKey) {
					if (isZero(good))
						return "Instable if has any compiler errors" + prefKeyToMessage(prefKey);
					else
						return "Instable if has more than " + format1000(good) + " compiler errors"
								+ prefKeyToMessage(prefKey);
				}
				
				@Override
				public String computeProblematicMessage(double warn, String[] prefKey) {
					if (isZero(warn))
						return "Should have no compiler errors" + prefKeyToMessage(prefKey);
					else
						return "Should not have more than " + format1000(warn) + " compiler errors"
								+ prefKeyToMessage(prefKey);
				}
			});
	
	private static boolean isZero(double warn) {
		return (int) warn == 0;
	}
	
	@Override
	public String getName() {
		return "Compiler errors";
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public List<BuildHealthPreference> getKnownPreferences() {
		List<BuildHealthPreference> result = new ArrayList<BuildHealthPreference>();
		
		result.add(new BuildHealthPreference("Maximun munber of compiler errors for a Good build", "0",
				"compilererrors", "good"));
		result.add(new BuildHealthPreference("Maximun munber of compiler errors for a So So build", "0",
				"compilererrors", "warn"));
		
		result.add(new BuildHealthPreference("Maximun munber of compiler errors for a Good build", "<no limit>",
				"compilererrors", ANY_VALUE_KEY_PREFIX + "<language>", "good"));
		result.add(new BuildHealthPreference("Maximun munber of compiler errors for a So So build", "<no limit>",
				"compilererrors", ANY_VALUE_KEY_PREFIX + "<language>", "warn"));
		
		result.add(new BuildHealthPreference("Maximun munber of compiler errors for a Good build", "<no limit>",
				"compilererrors", ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<framework>", "good"));
		result.add(new BuildHealthPreference("Maximun munber of compiler errors for a So So build", "<no limit>",
				"compilererrors", ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<framework>", "warn"));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public List<Report> computeReport(DataTable data, Projects projects, Preferences prefs, int opts) {
		data = data.filter("Compiler error");
		if (data.isEmpty())
			return Collections.emptyList();
		
		prefs = prefs.child("compilererrors");
		
		if (prefs.get("good", null) == null && prefs.get("warn", null) == null) {
			prefs.set("good", 0);
			prefs.set("warn", 0);
		}
		
		SimpleTree<Stats> tree = buildTree(data, projects);
		
		sumChildStatsAndComputeBuildStatuses(tree, prefs);
		
		return asList(toReport(tree.getRoot(), getName(), prefs, 1));
	}
	
	private SimpleTree<Stats> buildTree(DataTable data, Projects projects) {
		SimpleTree<Stats> tree = new SimpleTree<Stats>(new Function<String[], Stats>() {
			@Override
			public Stats apply(String[] name) {
				return new Stats(name);
			}
		});
		
		for (Line line : data.getLines()) {
			SimpleTree<Stats>.Node node = tree.getRoot();
			
			node = node.getChild(getLanguage(line));
			node = node.getChild(line.getColumn(COLUMN_FRAMEWORK));
			
			List<Location> locations = Location.parse(line.getColumn(COLUMN_LOCATION));
			String project = projects.findProjectForLocations(locations);
			if (project != null)
				node = node.getChild(project);
			
			String category = line.getColumn(COLUMN_CATEGORY);
			if (!category.isEmpty()) {
				for (String cat : category.split("/|\\\\")) {
					cat = cat.trim();
					if (cat.isEmpty())
						cat = "<no name>";
					node = node.getChild(cat);
				}
			}
			
			if (!locations.isEmpty())
				node.addUnnamedChild().getData().setViolation(line);
			else
				node.getData().addViolation(line);
		}
		
		return tree;
	}
	
	private static String getLanguage(Line line) {
		return firstNonEmpty(line.getColumn(COLUMN_LANGUAGE), "Unknown");
	}
	
	private void sumChildStatsAndComputeBuildStatuses(SimpleTree<Stats> tree, final Preferences prefs) {
		tree.visit(new SimpleTree.Visitor<Stats>() {
			Deque<Stats> parents = new ArrayDeque<Stats>();
			
			@Override
			public void preVisitNode(SimpleTree<Stats>.Node node) {
				Stats stats = node.getData();
				
				parents.push(stats);
			}
			
			@Override
			public void posVisitNode(SimpleTree<Stats>.Node node) {
				parents.pop();
				
				Stats stats = node.getData();
				
				stats.computeStatus(prefs);
				
				if (!parents.isEmpty())
					parents.peekFirst().addChild(stats);
			}
		});
	}
	
	private Report toReport(SimpleTree<Stats>.Node node, String name, Preferences prefs, int showAllFrameworks) {
		Stats stats = node.getData();
		
		if (stats.violation != null)
			return toViolation(stats, stats.violation);
		
		List<Report> children = new ArrayList<Report>();
		
		for (SimpleTree<Stats>.Node child : node.getChildren())
			children.add(toReport(child, child.getName(), prefs, showAllFrameworks - 1));
		
		sort(children);
		
		String description = "";
		if (showAllFrameworks > 0)
			description = toDescription(stats);
		
		return new Report(node.isRoot() ? stats.getStatusWithChildren() : stats.getStatus(), name,
				format1000(stats.getTotal()), description, stats.getProblemDescription(), children);
	}
	
	private void sort(List<Report> children) {
		Collections.sort(children, new Comparator<Report>() {
			@Override
			public int compare(Report o1, Report o2) {
				boolean r1S = o1 instanceof CompilerErrorReport;
				boolean r2S = o2 instanceof CompilerErrorReport;
				
				if (r1S != r2S)
					return (r1S ? 1 : 0) - (r2S ? 1 : 0);
				
				if (!r1S)
					return o1.getName().compareToIgnoreCase(o2.getName());
				
				CompilerErrorReport v1 = (CompilerErrorReport) o1;
				CompilerErrorReport v2 = (CompilerErrorReport) o1;
				
				List<Location> ls1 = v1.getLocations();
				List<Location> ls2 = v2.getLocations();
				
				if (ls1.isEmpty() != ls2.isEmpty())
					return ls1.isEmpty() ? 0 : 1;
				
				if (!ls1.isEmpty() && !ls2.isEmpty()) {
					Location l1 = ls1.get(0);
					Location l2 = ls2.get(0);
					
					int cmp = l1.file.compareToIgnoreCase(l2.file);
					if (cmp != 0)
						return cmp;
					
					cmp = l1.beginLine - l2.beginLine;
					if (cmp != 0)
						return cmp;
				}
				
				return v1.getMessage().compareToIgnoreCase(v2.getMessage());
			}
		});
	}
	
	private String toDescription(Stats stats) {
		List<Framework> fs = new ArrayList<Framework>(stats.frameworks.values());
		Collections.sort(fs, new Comparator<Framework>() {
			@Override
			public int compare(Framework o1, Framework o2) {
				int cmp = o1.language.compareToIgnoreCase(o2.language);
				if (cmp != 0)
					return cmp;
				
				return o1.framework.compareToIgnoreCase(o2.framework);
			}
		});
		
		StringBuilder result = new StringBuilder();
		
		int languages = countLanguages(fs);
		String oldLanguage = "";
		for (Framework f : fs) {
			if (languages > 1 && !oldLanguage.equals(f.language)) {
				oldLanguage = f.language;
				
				if (result.length() > 0)
					result.append(" ; ");
				
				result.append(f.language).append(": ");
				
			} else {
				if (result.length() > 0)
					result.append(", ");
			}
			
			result.append(f.framework).append(": ").append(format1000(f.total));
		}
		
		return result.toString();
	}
	
	private int countLanguages(List<Framework> fs) {
		String oldLanguage = null;
		int languages = 0;
		for (Framework f : fs) {
			if (oldLanguage == null || !oldLanguage.equals(f.language)) {
				languages++;
				oldLanguage = f.language;
			}
		}
		return languages;
	}
	
	private CompilerErrorReport toViolation(Stats stats, Line line) {
		String language = getLanguage(line);
		String framework = line.getColumn(COLUMN_FRAMEWORK);
		List<Location> locations = Location.parse(line.getColumn(COLUMN_LOCATION));
		String category = line.getColumn(COLUMN_CATEGORY);
		String message = line.getColumn(COLUMN_MESSAGE);
		String details = line.getColumn(COLUMN_DETAILS);
		
		return new CompilerErrorReport(stats.getStatusWithChildren(), language, framework, locations, category,
				message, details, stats.getProblemDescription());
	}
	
	private class Stats extends TreeStats {
		Line violation;
		final Map<String, Framework> frameworks = new HashMap<String, Framework>();
		
		Stats(String[] name) {
			super(name);
		}
		
		void addViolation(Line line) {
			getFramework(getLanguage(line), line.getColumn(COLUMN_FRAMEWORK)).total += line.getValue();
		}
		
		void setViolation(Line line) {
			this.violation = line;
			
			addViolation(line);
		}
		
		void addChild(Stats stats) {
			for (Framework framework : stats.frameworks.values())
				getFramework(framework.language, framework.framework).total += framework.total;
			
			mergeChildStatus(stats);
		}
		
		void computeStatus(Preferences prefs) {
			String[] names = getNames();
			double total = getTotal();
			
			BuildStatusAndExplanation status;
			
			if (violation != null)
				status = statusComputer.computeChild(total, prefs, names);
			else
				status = statusComputer.computeParent(total, getStatusWithChildren(), prefs, names);
			
			if (status != null)
				setOwnStatus(status);
		}
		
		double getTotal() {
			double total = 0;
			for (Framework framework : frameworks.values())
				total += framework.total;
			return total;
		}
		
		Framework getFramework(String language, String framework) {
			String key = language + "\n" + framework;
			
			Framework result = frameworks.get(key);
			if (result == null) {
				result = new Framework(language, framework);
				frameworks.put(key, result);
			}
			
			return result;
		}
	}
	
	private static class Framework {
		final String language;
		final String framework;
		double total = 0;
		
		Framework(String language, String framwork) {
			this.language = language;
			this.framework = framwork;
		}
	}
	
}
