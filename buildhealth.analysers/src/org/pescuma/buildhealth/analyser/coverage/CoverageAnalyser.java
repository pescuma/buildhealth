package org.pescuma.buildhealth.analyser.coverage;

import static com.google.common.base.MoreObjects.*;
import static java.lang.Math.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.pescuma.buildhealth.analyser.utils.NumbersFormater.*;
import static org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusMessageFormaterHelper.*;
import static org.pescuma.buildhealth.core.prefs.BuildHealthPreference.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.utils.BuildHealthAnalyserUtils.TreeStats;
import org.pescuma.buildhealth.analyser.utils.SimpleTree;
import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusAndExplanation;
import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusFromThresholdComputer;
import org.pescuma.buildhealth.analyser.utils.buildstatus.BuildStatusMessageFormater;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.projects.Projects;
import org.pescuma.datatable.DataTable;
import org.pescuma.datatable.DataTable.Line;

import com.google.common.base.Function;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Coverage,language,framework,{what:covered,total,type},{type:line,block,method,class},place,place,place,...
 * </pre>
 * 
 * The value is the number for covered or total entries. For all entries you must have both.
 * 
 * Place may contain multiple columns. The value for a line {a,b,c} is the total over all lines(a,b,c,**}. You should
 * always have a place type all.
 * 
 * Example:
 * 
 * <pre>
 * 10 | Coverage,Java,Emma,covered,line
 * 15 | Coverage,Java,Emma,total,line
 * 0 | Coverage,Java,Emma,type,class,a,b,c,D
 * 1 | Coverage,Java,Emma,covered,line,a,b,c,D
 * 2 | Coverage,Java,Emma,total,line,a,b,c,D
 * </pre>
 */
@MetaInfServices
public class CoverageAnalyser implements BuildHealthAnalyser {
	
	public static final int COLUMN_LANGUAGE = 1;
	public static final int COLUMN_FRAMEWORK = 2;
	public static final int COLUMN_WHAT = 3;
	public static final int COLUMN_TYPE = 4;
	public static final int COLUMN_PLACE_START = 5;
	
	public static final String COVERED = "covered";
	public static final String TOTAL = "total";
	public static final String TYPE = "type";
	
	private static final String DEFAULT_MAINTYPE = "instruction,line";
	
	private boolean showDetailsInDescription = false;
	
	private final BuildStatusFromThresholdComputer statusComputer = new BuildStatusFromThresholdComputer(true,
			new BuildStatusMessageFormater() {
				@Override
				public String computeSoSoMessage(double good, String[] prefKey) {
					return getName(prefKey) + " is unstable if less than " + formatValue(good);
				}
				
				@Override
				public String computeProblematicMessage(double warn, String[] prefKey) {
					return getName(prefKey) + " should not be less than " + formatValue(warn);
				}
				
				private String formatValue(double value) {
					return format1000(value) + "%";
				}
				
				private String getName(String[] prefKey) {
					if (prefKey.length < 2)
						return "Coverage";
					
					Deque<String> pieces = new LinkedList<String>(asList(prefKey));
					
					StringBuilder result = new StringBuilder();
					result.append(StringUtils.capitalize(pieces.removeLast()));
					result.append(" coverage");
					result.append(prefKeyToMessage(pieces));
					
					return result.toString();
				}
			});
	
	public void setShowDetailsInDescription(boolean showDetailsInDescription) {
		this.showDetailsInDescription = showDetailsInDescription;
	}
	
	@Override
	public String getName() {
		return "Coverage";
	}
	
	@Override
	public int getPriority() {
		return 200;
	}
	
	@Override
	public List<BuildHealthPreference> getKnownPreferences() {
		List<BuildHealthPreference> result = new ArrayList<BuildHealthPreference>();
		
		result.add(new BuildHealthPreference("Minimun coverage for a Good build", "<no limit>", "coverage", "good"));
		result.add(new BuildHealthPreference("Minimun coverage for a So So build", "<no limit>", "coverage", "warn"));
		
		result.add(new BuildHealthPreference("Minimun coverage for a Good build", "<no limit>", "coverage",
				ANY_VALUE_KEY_PREFIX + "<type>", "good"));
		result.add(new BuildHealthPreference("Minimun coverage for a So So build", "<no limit>", "coverage",
				ANY_VALUE_KEY_PREFIX + "<type>", "warn"));
		
		result.add(new BuildHealthPreference("Minimun coverage for a Good build", "<no limit>", "coverage",
				ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<type>", "good"));
		result.add(new BuildHealthPreference("Minimun coverage for a So So build", "<no limit>", "coverage",
				ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<type>", "warn"));
		
		result.add(new BuildHealthPreference("Minimun coverage for a Good build", "<no limit>", "coverage",
				ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<framework>", ANY_VALUE_KEY_PREFIX
						+ "<type>", "good"));
		result.add(new BuildHealthPreference("Minimun coverage for a So So build", "<no limit>", "coverage",
				ANY_VALUE_KEY_PREFIX + "<language>", ANY_VALUE_KEY_PREFIX + "<framework>", ANY_VALUE_KEY_PREFIX
						+ "<type>", "warn"));
		
		result.add(new BuildHealthPreference(
				"Which coverage type will represent the global coverage (can have more than one, separated by ',', with the most important first)",
				DEFAULT_MAINTYPE, "coverage", "maintype"));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public List<Report> computeReport(DataTable data, Projects projects, Preferences prefs, int opts) {
		data = data.filter("Coverage");
		if (data.isEmpty())
			return Collections.emptyList();
		
		prefs = prefs.child("coverage");
		
		List<String> preferredCoverageTypes = getPreferredCoverageTypes(prefs);
		
		SimpleTree<Stats> tree = buildTree(data, projects);
		
		sumChildStatsAndComputeBuildStatuses(tree, prefs, preferredCoverageTypes);
		
		CoverageReport result = toReport(tree.getRoot(), getName(), prefs, preferredCoverageTypes);
		if (result == null)
			return emptyList();
		else
			return asList((Report) result);
	}
	
	private List<String> getPreferredCoverageTypes(Preferences prefs) {
		List<String> preferredCoverageTypes = new ArrayList<String>();
		
		for (String type : prefs.get("maintype", DEFAULT_MAINTYPE).split(","))
			preferredCoverageTypes.add(type);
		
		return preferredCoverageTypes;
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
			
			node = node.getChild(line.getColumn(COLUMN_LANGUAGE));
			node = node.getChild(line.getColumn(COLUMN_FRAMEWORK));
			
			String[] columns = line.getColumns();
			for (int i = COLUMN_PLACE_START; i < columns.length; i++)
				node = node.getChild(columns[i]);
			
			Stats stats = node.getData();
			
			String type = line.getColumn(COLUMN_TYPE);
			if (type.isEmpty())
				continue;
			
			String what = line.getColumn(COLUMN_WHAT);
			
			if (TYPE.equals(what)) {
				stats.placeType = type;
				continue;
			}
			
			boolean covered = true;
			if (COVERED.equals(what))
				covered = true;
			else if (TOTAL.equals(what))
				covered = false;
			else
				continue;
			
			CoverageTypeStats coverage = stats.getCoverage(type);
			if (covered)
				coverage.addCovered(line.getValue());
			else
				coverage.addTotal(line.getValue());
		}
		
		return tree;
	}
	
	private void sumChildStatsAndComputeBuildStatuses(SimpleTree<Stats> tree, final Preferences prefs,
			final List<String> preferredCoverageTypes) {
		
		tree.visit(new SimpleTree.Visitor<Stats>() {
			Deque<Stats> stack = new ArrayDeque<Stats>();
			
			@Override
			public void preVisitNode(SimpleTree<Stats>.Node node) {
				stack.push(node.getData());
			}
			
			@Override
			public void posVisitNode(SimpleTree<Stats>.Node node) {
				stack.pop();
				
				Stats stats = node.getData();
				
				computeBuildStatus(stats, prefs, preferredCoverageTypes);
				
				if (stack.isEmpty())
					return;
				
				Stats parent = stack.peek();
				
				for (String type : stats.coverages.keySet())
					parent.getCoverage(type).addChild(stats.getCoverage(type));
			}
		});
	}
	
	private void computeBuildStatus(Stats stats, Preferences prefs, List<String> preferredCoverageTypes) {
		CoverageTypeStats prefCoverage = findPrefered(stats, preferredCoverageTypes);
		if (prefCoverage != null)
			computeBuildStatus(stats, prefCoverage, prefs);
		
		for (String coverageType : stats.coverages.keySet()) {
			CoverageTypeStats coverage = stats.getCoverage(coverageType);
			computeBuildStatus(stats, coverage, prefs, coverageType);
		}
		
		for (CoverageTypeStats coverage : stats.coverages.values())
			stats.mergeChildStatus(coverage);
	}
	
	private CoverageTypeStats findPrefered(Stats stats, List<String> preferredCoverageTypes) {
		for (String coverageType : preferredCoverageTypes) {
			if (stats.hasCoverage(coverageType)) {
				CoverageTypeStats result = stats.getCoverage(coverageType);
				if (result.hasData())
					return result;
			}
		}
		
		return null;
	}
	
	private void computeBuildStatus(Stats stats, CoverageTypeStats coverage, Preferences prefs, String... subPrefKeys) {
		if (!coverage.hasData())
			// not enough data
			return;
		
		BuildStatusAndExplanation status = statusComputer.compute(coverage.getPercentage(), prefs, stats.getNames(),
				subPrefKeys);
		
		if (status != null) {
			coverage.setOwnStatus(status);
			stats.setOwnStatus(status);
		}
	}
	
	private CoverageReport toReport(SimpleTree<Stats>.Node node, String name, Preferences prefs,
			List<String> preferredCoverageTypes) {
		
		Stats stats = node.getData();
		
		List<CoverageMetric> coverageMetrics = getCoverageMetrics(stats);
		
		if (coverageMetrics.size() < 1)
			return null;
		
		String description = getDescription(stats, coverageMetrics);
		
		CoverageMetric defCoverage = getDefaultCoverage(coverageMetrics, preferredCoverageTypes);
		
		List<CoverageReport> children = new ArrayList<CoverageReport>();
		for (SimpleTree<Stats>.Node child : node.getChildren()) {
			CoverageReport report = toReport(child, child.getName(), prefs, preferredCoverageTypes);
			if (report != null)
				children.add(report);
		}
		
		return new CoverageReport(node.isRoot() ? stats.getStatusWithChildren() : stats.getStatus(), name,
				defCoverage.getPercentage() + "%", description, coverageMetrics, stats.placeType,
				stats.getProblemDescription(), children);
	}
	
	private List<CoverageMetric> getCoverageMetrics(Stats stats) {
		List<CoverageMetric> coverageMetrics = new ArrayList<CoverageMetric>();
		
		for (CoverageTypeStats coverage : sortCoverageTypes(stats.coverages.values()))
			coverageMetrics.add(new CoverageMetric(coverage.getName(), coverage.covered, coverage.total));
		
		return coverageMetrics;
	}
	
	private Collection<CoverageTypeStats> sortCoverageTypes(Collection<CoverageTypeStats> coverages) {
		final Map<String, Integer> fixed = new HashMap<String, Integer>();
		fixed.put("class", 0);
		fixed.put("method", 1);
		fixed.put("block", 2);
		fixed.put("line", 3);
		fixed.put("branch", 4);
		fixed.put("instruction", 5);
		
		List<CoverageTypeStats> result = new ArrayList<CoverageTypeStats>(coverages);
		
		Collections.sort(result, new Comparator<CoverageTypeStats>() {
			@Override
			public int compare(CoverageTypeStats o1, CoverageTypeStats o2) {
				String n1 = o1.getName();
				String n2 = o2.getName();
				
				int t1 = firstNonNull(fixed.get(n1), fixed.size());
				int t2 = firstNonNull(fixed.get(n2), fixed.size());
				if (t1 != t2)
					return t1 - t2;
				
				return n1.compareToIgnoreCase(n2);
			}
		});
		
		return result;
	}
	
	private String getDescription(Stats stats, List<CoverageMetric> coverageMetrics) {
		StringBuilder description = new StringBuilder();
		
		for (CoverageMetric coverage : coverageMetrics) {
			if (description.length() > 0)
				description.append(", ");
			
			description.append(coverage.getName()).append(": ");
			if (coverage.hasTotal())
				description.append(coverage.getPercentage()).append("%");
			else
				description.append("-");
			
			if (showDetailsInDescription)
				description.append(" (").append(format1000(coverage.getCovered())).append("/")
						.append(format1000(coverage.getTotal())).append(")");
			
		}
		
		if (!showDetailsInDescription && stats.hasCoverage("line")) {
			long lines = round(stats.getCoverage("line").total);
			if (lines > 0)
				description.append(", over ").append(format1000(lines)).append(" ")
						.append(lines > 1 ? "lines" : "line");
		}
		
		return description.toString();
	}
	
	private CoverageMetric getDefaultCoverage(List<CoverageMetric> coverageMetrics, List<String> preferredCoverageTypes) {
		CoverageMetric result = null;
		int resultIndex = Integer.MAX_VALUE;
		
		for (CoverageMetric coverage : coverageMetrics) {
			if (!coverage.hasTotal())
				continue;
			
			int index = preferredCoverageTypes.indexOf(coverage.getName());
			if (index == -1)
				index = Integer.MAX_VALUE;
			
			if (index <= resultIndex) {
				result = coverage;
				resultIndex = index;
			}
		}
		
		if (result == null)
			for (CoverageMetric coverage : coverageMetrics)
				if (coverage.hasTotal())
					result = coverage;
		
		if (result == null)
			result = coverageMetrics.get(0);
		
		return result;
	}
	
	private static class Stats extends TreeStats {
		
		String placeType;
		final Map<String, CoverageTypeStats> coverages = new HashMap<String, CoverageTypeStats>();
		
		Stats(String[] name) {
			super(name);
		}
		
		boolean hasCoverage(String type) {
			return coverages.containsKey(type);
		}
		
		CoverageTypeStats getCoverage(String type) {
			CoverageTypeStats result = coverages.get(type);
			if (result == null) {
				result = new CoverageTypeStats(type);
				coverages.put(type, result);
			}
			return result;
		}
	}
	
	private static class CoverageTypeStats extends TreeStats {
		
		boolean hasCovered = false;
		double covered = 0;
		boolean hasTotal = false;
		double total = 0;
		
		CoverageTypeStats(String type) {
			super(type);
		}
		
		String getName() {
			return getNames()[0];
		}
		
		void addChild(CoverageTypeStats other) {
			if (!hasCovered)
				covered += other.covered;
			
			if (!hasTotal)
				total += other.total;
			
			mergeChildStatus(other);
		}
		
		void addCovered(double value) {
			hasCovered = true;
			covered += value;
		}
		
		void addTotal(double value) {
			hasTotal = true;
			total += value;
		}
		
		int getPercentage() {
			if (total < 0.001)
				return 100;
			else
				return (int) round(100 * covered / total);
		}
		
		boolean hasData() {
			return total > 0.0001;
		}
	}
}
