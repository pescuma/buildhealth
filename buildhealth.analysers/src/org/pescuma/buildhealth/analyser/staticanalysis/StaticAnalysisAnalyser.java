package org.pescuma.buildhealth.analyser.staticanalysis;

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
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.utils.Location;

import com.google.common.base.Function;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Static analysis,language,framework,location,category,message,{severity:Low,Medium,High},details,URL with details
 * </pre>
 * 
 * The number is the number of violations in that place.
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
 * 1 | Static analysis,Java,Task,/a/b.java,12,Type1,Go to bed
 * 1 | Static analysis,Java,PMD,/a/b.java,12:1:12:5,A/Type1,Go to bed,http://a.com/info.html
 * 10 | Static analysis,Java,Task,/a/c.java,,Type2
 * </pre>
 */
@MetaInfServices
public class StaticAnalysisAnalyser implements BuildHealthAnalyser {
	
	public static final int COLUMN_LANGUAGE = 1;
	public static final int COLUMN_FRAMEWORK = 2;
	public static final int COLUMN_LOCATION = 3;
	public static final int COLUMN_CATEGORY = 4;
	public static final int COLUMN_MESSAGE = 5;
	public static final int COLUMN_SEVERITY = 6;
	public static final int COLUMN_DETAILS = 7;
	public static final int COLUMN_URL = 8;
	
	private boolean usingSeverity;
	
	private final BuildStatusFromThresholdComputerConsideringParents statusComputer = new BuildStatusFromThresholdComputerConsideringParents(
			new BuildStatusMessageFormater() {
				@Override
				public String computeSoSoMessage(double good, String[] prefKey) {
					if (isZero(good))
						return "Instable if has any violations" + detailsFrom(prefKey);
					else
						return "Instable if has more than " + format1000(good) + " violations" + detailsFrom(prefKey);
				}
				
				@Override
				public String computeProblematicMessage(double warn, String[] prefKey) {
					if (isZero(warn))
						return "Should have no violations" + detailsFrom(prefKey);
					else
						return "Should not have more than " + format1000(warn) + " violations" + detailsFrom(prefKey);
				}
				
				private String detailsFrom(String[] prefKey) {
					if (usingSeverity)
						return prefKeyWithSeverityToMessage(prefKey);
					else
						return prefKeyToMessage(prefKey);
				}
			});
	
	private static boolean isZero(double warn) {
		return (int) warn == 0;
	}
	
	@Override
	public String getName() {
		return "Static analysis";
	}
	
	@Override
	public int getPriority() {
		return 300;
	}
	
	@Override
	public List<BuildHealthPreference> getPreferences() {
		List<BuildHealthPreference> result = new ArrayList<BuildHealthPreference>();
		
		result.add(new BuildHealthPreference("Maximun munber of violations for a Good build", "<no limit>",
				"staticanalysis", "good"));
		result.add(new BuildHealthPreference("Maximun munber of violations for a So So build", "<no limit>",
				"staticanalysis", "warn"));
		
		result.add(new BuildHealthPreference("Maximun munber of violations for a Good build", "<no limit>",
				"staticanalysis", ANY_VALUE_KEY_PREFIX + "<language>", "good"));
		result.add(new BuildHealthPreference("Maximun munber of violations for a So So build", "<no limit>",
				"staticanalysis", ANY_VALUE_KEY_PREFIX + "<language>", "warn"));
		
		result.add(new BuildHealthPreference("Maximun munber of violations for a Good build", "<no limit>",
				"staticanalysis", ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<framework>", "good"));
		result.add(new BuildHealthPreference("Maximun munber of violations for a So So build", "<no limit>",
				"staticanalysis", ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<framework>", "warn"));
		
		result.add(new BuildHealthPreference("Maximun munber of violations for a Good build", "<no limit>",
				"staticanalysis", ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<framework>", "good"));
		result.add(new BuildHealthPreference("Maximun munber of violations for a So So build", "<no limit>",
				"staticanalysis", ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<framework>", "warn"));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public List<Report> computeReport(BuildData data, Preferences prefs, int opts) {
		data = data.filter("Static analysis");
		if (data.isEmpty())
			return Collections.emptyList();
		
		prefs = prefs.child("staticanalysis");
		
		SimpleTree<Stats> tree = buildTree(data);
		
		sumChildStatsAndComputeBuildStatuses(tree, prefs);
		
		return asList(toReport(tree.getRoot(), getName(), prefs, 1));
	}
	
	private SimpleTree<Stats> buildTree(BuildData data) {
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
			
			String category = line.getColumn(COLUMN_CATEGORY);
			if (!category.isEmpty()) {
				for (String cat : category.split("/|\\\\")) {
					cat = cat.trim();
					if (cat.isEmpty())
						cat = "<no name>";
					node = node.getChild(cat);
				}
			}
			
			if (!line.getColumn(COLUMN_LOCATION).isEmpty())
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
				boolean r1S = o1 instanceof StaticAnalysisViolation;
				boolean r2S = o2 instanceof StaticAnalysisViolation;
				
				if (r1S != r2S)
					return (r1S ? 1 : 0) - (r2S ? 1 : 0);
				
				if (!r1S)
					return o1.getName().compareToIgnoreCase(o2.getName());
				
				StaticAnalysisViolation v1 = (StaticAnalysisViolation) o1;
				StaticAnalysisViolation v2 = (StaticAnalysisViolation) o1;
				
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
					result.append("; ");
				
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
	
	private StaticAnalysisViolation toViolation(Stats stats, Line line) {
		String language = getLanguage(line);
		String framework = line.getColumn(COLUMN_FRAMEWORK);
		List<Location> locations = Location.parse(line.getColumn(COLUMN_LOCATION));
		String category = line.getColumn(COLUMN_CATEGORY);
		String message = line.getColumn(COLUMN_MESSAGE);
		String severity = line.getColumn(COLUMN_SEVERITY);
		String details = line.getColumn(COLUMN_DETAILS);
		String url = line.getColumn(COLUMN_URL);
		
		return new StaticAnalysisViolation(stats.violationStatus.status, language, framework, locations, category,
				message, severity, details, url, stats.violationStatus.explanation);
	}
	
	private class Stats extends TreeStats {
		Line violation;
		final Map<String, Framework> frameworks = new HashMap<String, Framework>();
		final Map<String, Severity> severities = new HashMap<String, Severity>();
		BuildStatusAndExplanation violationStatus = new BuildStatusAndExplanation(BuildStatus.Good, null);
		
		Stats(String[] name) {
			super(name);
		}
		
		void addViolation(Line line) {
			getFramework(getLanguage(line), line.getColumn(COLUMN_FRAMEWORK)).total += line.getValue();
			
			String severity = line.getColumn(COLUMN_SEVERITY);
			if (!severity.isEmpty())
				getSeverity(severity).total += line.getValue();
		}
		
		void setViolation(Line line) {
			this.violation = line;
			
			addViolation(line);
		}
		
		void addChild(Stats stats) {
			for (Framework framework : stats.frameworks.values())
				getFramework(framework.language, framework.framework).total += framework.total;
			
			for (Severity severity : stats.severities.values())
				getSeverity(severity.name).total += severity.total;
			
			mergeChildStatus(stats);
		}
		
		void computeStatus(Preferences prefs) {
			String[] names = getNames();
			double total = getTotal();
			
			BuildStatusAndExplanation status;
			
			if (violation != null) {
				String severity = violation.getColumn(COLUMN_SEVERITY);
				if (!severity.isEmpty())
					status = computeChild(total, prefs, names, severity);
				else
					status = computeChild(total, prefs, names, null);
				
			} else {
				BuildStatus statusWithChildren = getStatusWithChildren();
				
				List<BuildStatusAndExplanation> statuses = new ArrayList<BuildStatusAndExplanation>();
				statuses.add(computeParent(total, statusWithChildren, prefs, names, null));
				for (Severity severity : severities.values())
					statuses.add(computeParent(severity.total, statusWithChildren, prefs, names, severity.name));
				
				status = findWorse(statuses);
			}
			
			if (status != null)
				setOwnStatus(status);
		}
		
		private BuildStatusAndExplanation computeParent(double total, BuildStatus statusWithChildren,
				Preferences prefs, String[] names, String severity) {
			if (severity != null) {
				usingSeverity = true;
				try {
					return statusComputer.computeParent(total, statusWithChildren, prefs, names, severity);
				} finally {
					usingSeverity = false;
				}
			} else {
				return statusComputer.computeParent(total, statusWithChildren, prefs, names);
			}
		}
		
		private BuildStatusAndExplanation computeChild(double total, Preferences prefs, String[] names, String severity) {
			if (severity != null) {
				usingSeverity = true;
				try {
					return statusComputer.computeChild(total, prefs, names, severity);
				} finally {
					usingSeverity = false;
				}
			} else {
				return statusComputer.computeChild(total, prefs, names);
			}
		}
		
		private BuildStatusAndExplanation findWorse(List<BuildStatusAndExplanation> statuses) {
			BuildStatusAndExplanation result = null;
			
			for (BuildStatusAndExplanation status : statuses) {
				if (status == null)
					continue;
				
				else if (status.status == BuildStatus.Problematic)
					return status;
				
				else if (result == null)
					result = status;
				
				else if (result.status == BuildStatus.Good)
					result = status;
			}
			
			return result;
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
		
		Severity getSeverity(String name) {
			Severity result = severities.get(name);
			if (result == null) {
				result = new Severity(name);
				severities.put(name, result);
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
	
	private static class Severity {
		final String name;
		double total = 0;
		
		public Severity(String name) {
			this.name = name;
		}
	}
	
}
