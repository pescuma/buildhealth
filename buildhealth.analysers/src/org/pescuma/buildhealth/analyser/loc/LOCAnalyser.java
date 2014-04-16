package org.pescuma.buildhealth.analyser.loc;

import static java.lang.Math.*;
import static java.util.Arrays.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.coverage.CoverageAnalyser;
import org.pescuma.buildhealth.analyser.utils.BuildHealthAnalyserUtils.TreeStats;
import org.pescuma.buildhealth.analyser.utils.NumbersFormater;
import org.pescuma.buildhealth.analyser.utils.SimpleTree;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.core.BuildData.Value;
import org.pescuma.buildhealth.core.BuildHealth.ReportFlags;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.projects.Projects;

import com.google.common.base.Function;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * LOC,language,type,file or folder name (optional)
 * </pre>
 * 
 * All values are in lines.
 * 
 * Usual values for type are: source, comment, blank, unknown. There is a special type called files that means the
 * number of files (and not a line count).
 * 
 * Example:
 * 
 * <pre>
 * 100 | LOC,Java,comment
 * 900 | LOC,Java,code
 * 10 | LOC,C#,blank
 * 12 | LOC,Java,unknown,/tmp/X
 * 15 | LOC,Java,files,/tmp/X
 * 12 | LOC,C++,files
 * </pre>
 * 
 * If not LOC information is found, it will try to get this information from coverage data.
 */
@MetaInfServices
public class LOCAnalyser implements BuildHealthAnalyser {
	
	private static final int COLUMN_LANGUAGE = 1;
	private static final int COLUMN_TYPE = 2;
	private static final int COLUMN_FILE = 3;
	
	private static final String TYPE_FILES = "files";
	private static final String TYPE_UNKNOWN = "unknown";
	
	@Override
	public String getName() {
		return "Lines of code";
	}
	
	@Override
	public int getPriority() {
		return 500;
	}
	
	@Override
	public List<BuildHealthPreference> getKnownPreferences() {
		return Collections.emptyList();
	}
	
	@Override
	public List<Report> computeReport(BuildData data, Projects projects, Preferences prefs, int opts) {
		boolean summaryOnly = (opts & ReportFlags.SummaryOnly) != 0;
		
		List<Report> result = computeFromLOC(data, projects, summaryOnly);
		
		if (result.isEmpty())
			result = computeFromCoverage(data, summaryOnly);
		
		return result;
	}
	
	private List<Report> computeFromCoverage(BuildData data, boolean summaryOnly) {
		data = data.filter("Coverage").filter(CoverageAnalyser.COLUMN_PLACE_START, "")
				.filter(CoverageAnalyser.COLUMN_TYPE, "line").filter(CoverageAnalyser.COLUMN_WHAT, "total");
		if (data.isEmpty())
			return Collections.emptyList();
		
		long lines = round(data.sum());
		if (lines < 1)
			return Collections.emptyList();
		
		List<Report> children = (summaryOnly ? null : computeLanguagesFromCoverage(data));
		return asList(new Report(Good, getName(), format(lines), "from coverage", null, children));
		
	}
	
	private List<Report> computeLanguagesFromCoverage(BuildData data) {
		List<Report> children = new ArrayList<Report>();
		
		for (Map.Entry<String, Value> language : data.sumDistinct(COLUMN_LANGUAGE).entrySet())
			children.add(new Report(Good, language.getKey(), format(language.getValue().value)));
		
		return children;
	}
	
	private List<Report> computeFromLOC(BuildData data, Projects projects, boolean summaryOnly) {
		data = data.filter("LOC");
		if (data.isEmpty())
			return Collections.emptyList();
		
		SimpleTree<Stats> tree = buildTree(data, projects);
		
		sumChildStats(tree);
		
		return asList(toReport(tree.getRoot(), getName()));
	}
	
	private SimpleTree<Stats> buildTree(BuildData data, Projects projects) {
		SimpleTree<Stats> tree = new SimpleTree<Stats>(new Function<String[], Stats>() {
			@Override
			public Stats apply(String[] name) {
				return new Stats();
			}
		});
		
		for (Line line : data.getLines()) {
			SimpleTree<Stats>.Node node = tree.getRoot();
			
			node = node.getChild(line.getColumn(COLUMN_LANGUAGE));
			
			String file = line.getColumn(COLUMN_FILE);
			
			String project = projects.findProjectForFile(file);
			if (project != null)
				node = node.getChild(project);
			
			String type = line.getColumn(COLUMN_TYPE);
			
			if (type.equalsIgnoreCase(TYPE_FILES))
				node.getData().files += line.getValue();
			else
				node.getData().getType(type).total += line.getValue();
		}
		
		return tree;
	}
	
	private void sumChildStats(SimpleTree<Stats> tree) {
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
				
				if (!parents.isEmpty())
					parents.peekFirst().addChild(stats);
			}
		});
	}
	
	private Report toReport(SimpleTree<Stats>.Node node, String name) {
		Stats stats = node.getData();
		
		List<Type> types = sort(stats.types.values());
		
		List<Report> children = new ArrayList<Report>();
		
		if (node.getChildren().isEmpty()) {
			for (Type type : types)
				children.add(new Report(Good, type.name, format(type.total)));
			
			if ((int) stats.files > 0)
				children.add(new Report(Good, "Files", format(stats.files)));
			
		} else {
			for (SimpleTree<Stats>.Node child : node.getChildren())
				children.add(toReport(child, child.getName()));
		}
		
		double total = 0;
		for (Type type : types)
			total += type.total;
		
		StringBuilder description = new StringBuilder();
		
		if (stats.types.size() > 1)
			for (Type type : types)
				append(description, type.total, type.name);
		
		if ((int) stats.files > 0)
			append(description, "in", stats.files, "file", "files");
		
		return new Report(stats.getStatusWithChildren(), name, format(total), description.toString(),
				stats.getProblemDescription(), children);
	}
	
	private List<Type> sort(Collection<Type> values) {
		List<Type> result = new ArrayList<Type>(values);
		
		Collections.sort(result, getComparatorWithFixedEnding(TYPE_UNKNOWN));
		
		return result;
	}
	
	private Comparator<Type> getComparatorWithFixedEnding(final String... lastOnes) {
		return new Comparator<Type>() {
			@Override
			public int compare(Type o1, Type o2) {
				int i1 = indexOf(lastOnes, o1.name);
				int i2 = indexOf(lastOnes, o2.name);
				
				if (i1 != i2)
					return i1 - i2;
				
				return o1.name.compareToIgnoreCase(o2.name);
			}
			
			private int indexOf(String[] lastOnes, String o) {
				for (int i = 0; i < lastOnes.length; i++) {
					if (lastOnes[i].equalsIgnoreCase(o))
						return i;
				}
				return -1;
			}
		};
	}
	
	private void append(StringBuilder out, double value, String name) {
		append(out, null, value, name, name);
	}
	
	private void append(StringBuilder out, String prefix, double value, String singular, String plural) {
		if (out.length() > 0)
			out.append(", ");
		if (prefix != null)
			out.append(prefix).append(" ");
		out.append(format(value));
		out.append(" ");
		out.append(pluralize(value, singular, plural));
	}
	
	private String pluralize(double val, String singular, String plural) {
		return format(val).equals("1") ? singular : plural;
	}
	
	private String format(double total) {
		return NumbersFormater.format1000(total, "");
	}
	
	private class Stats extends TreeStats {
		final Map<String, Type> types = new HashMap<String, Type>();
		double files = 0;
		
		void addChild(Stats stats) {
			for (Type lang : stats.types.values())
				getType(lang.name).total += lang.total;
			
			files += stats.files;
		}
		
		Type getType(String name) {
			Type result = types.get(name);
			if (result == null) {
				result = new Type(name);
				types.put(name, result);
			}
			return result;
		}
	}
	
	private static class Type {
		final String name;
		double total = 0;
		
		public Type(String name) {
			this.name = name;
		}
	}
	
}
