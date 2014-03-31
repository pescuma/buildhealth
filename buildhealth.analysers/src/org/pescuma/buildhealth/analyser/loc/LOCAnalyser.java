package org.pescuma.buildhealth.analyser.loc;

import static java.lang.Math.*;
import static java.util.Arrays.*;
import static org.pescuma.buildhealth.core.BuildStatus.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.coverage.CoverageAnalyser;
import org.pescuma.buildhealth.analyser.utils.NumbersFormater;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Value;
import org.pescuma.buildhealth.core.BuildHealth.ReportFlags;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;

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
	public List<BuildHealthPreference> getPreferences() {
		return Collections.emptyList();
	}
	
	@Override
	public List<Report> computeReport(BuildData data, Preferences prefs, int opts) {
		boolean summaryOnly = (opts & ReportFlags.SummaryOnly) != 0;
		
		List<Report> result = computeFromLOC(data, summaryOnly);
		
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
	
	private List<Report> computeFromLOC(BuildData data, boolean summaryOnly) {
		data = data.filter("LOC");
		if (data.isEmpty())
			return Collections.emptyList();
		
		Map<String, BuildData.Value> vals = data.sumDistinct(COLUMN_TYPE);
		
		Value files = vals.remove(TYPE_FILES);
		
		int count = vals.size();
		
		double total = 0;
		for (Map.Entry<String, BuildData.Value> val : vals.entrySet())
			total += val.getValue().value;
		
		StringBuilder description = new StringBuilder();
		
		if (count > 1) {
			for (Map.Entry<String, BuildData.Value> val : vals.entrySet())
				append(description, val.getValue().value, val.getKey());
		}
		
		if (files != null)
			append(description, "in", round(files.value), "file", "files");
		
		List<Report> children = (summaryOnly ? null : computeLanguages(data));
		return asList(new Report(Good, "Lines of code", format(total), description.toString(), null, children));
	}
	
	private List<Report> computeLanguages(BuildData data) {
		List<Report> result = new ArrayList<Report>();
		
		Map<String[], BuildData.Value> vals = data.sumDistinct(COLUMN_LANGUAGE, COLUMN_TYPE);
		
		Map<String, Map<String, BuildData.Value>> tree = toTree(vals);
		
		for (Map.Entry<String, Map<String, BuildData.Value>> langEntry : tree.entrySet()) {
			String langauge = langEntry.getKey();
			List<Report> children = new ArrayList<Report>();
			
			double total = 0;
			int files = 0;
			for (Map.Entry<String, BuildData.Value> typeEntry : langEntry.getValue().entrySet()) {
				String type = typeEntry.getKey();
				double value = typeEntry.getValue().value;
				children.add(new Report(Good, StringUtils.capitalize(type), format(value)));
				
				if (!type.equalsIgnoreCase(TYPE_FILES))
					total += value;
			}
			
			StringBuilder description = new StringBuilder();
			if (files > 0)
				append(description, "in", files, "file", "files");
			
			result.add(new Report(Good, langauge, format(total), description.toString(), null, children));
		}
		
		return result;
	}
	
	private Map<String, Map<String, BuildData.Value>> toTree(Map<String[], BuildData.Value> vals) {
		Map<String, Map<String, BuildData.Value>> tree = new TreeMap<String, Map<String, Value>>(
				String.CASE_INSENSITIVE_ORDER);
		
		for (Map.Entry<String[], BuildData.Value> entry : vals.entrySet()) {
			String[] names = entry.getKey();
			
			Map<String, Value> lang = getLanguage(tree, names[0]);
			Value value = getType(lang, names[1]);
			
			value.value += entry.getValue().value;
		}
		return tree;
	}
	
	private Map<String, Value> getLanguage(Map<String, Map<String, BuildData.Value>> tree, String name) {
		Map<String, Value> lang = tree.get(name);
		if (lang == null) {
			lang = new TreeMap<String, BuildData.Value>(getComparatorWithFixedEnding(TYPE_UNKNOWN, TYPE_FILES));
			tree.put(name, lang);
		}
		return lang;
	}
	
	private Value getType(Map<String, Value> lang, String name) {
		Value value = lang.get(name);
		if (value == null) {
			value = new Value();
			lang.put(name, value);
		}
		return value;
	}
	
	private Comparator<String> getComparatorWithFixedEnding(final String... lastOnes) {
		return new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int i1 = indexOf(lastOnes, o1);
				int i2 = indexOf(lastOnes, o2);
				
				if (i1 != i2)
					return i1 - i2;
				
				return o1.compareToIgnoreCase(o2);
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
	
}
