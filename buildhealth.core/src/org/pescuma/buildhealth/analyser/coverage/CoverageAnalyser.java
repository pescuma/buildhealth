package org.pescuma.buildhealth.analyser.coverage;

import static com.google.common.base.Objects.*;
import static java.lang.Math.*;
import static java.util.Arrays.*;
import static org.pescuma.buildhealth.analyser.NumbersFormater.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pescuma.buildhealth.analyser.BaseBuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.BuildHealthAnalyserPreference;
import org.pescuma.buildhealth.analyser.NumbersFormater;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Value;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.prefs.Preferences;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Coverage,language,framework,{type:line,block,method,class},{covered,total},{place type:all,group,file,package,class,method},place
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
 * 10 | Coverage,Java,Emma,line,covered,all
 * 15 | Coverage,Java,Emma,line,total,all
 * 1 | Coverage,Java,Emma,line,covered,class,a,b,c,D
 * 2 | Coverage,Java,Emma,line,total,class,a,b,c,D
 * </pre>
 */
public class CoverageAnalyser extends BaseBuildHealthAnalyser {
	
	private boolean showDetailsInDescription = false;
	
	public void setShowDetailsInDescription(boolean showDetailsInDescription) {
		this.showDetailsInDescription = showDetailsInDescription;
	}
	
	@Override
	public String getName() {
		return "Coverage";
	}
	
	@Override
	public List<BuildHealthAnalyserPreference> getPreferences() {
		List<BuildHealthAnalyserPreference> result = new ArrayList<BuildHealthAnalyserPreference>();
		
		result.add(new BuildHealthAnalyserPreference("Minimun coverage for a Good build", "<no limit>", "coverage",
				"good"));
		result.add(new BuildHealthAnalyserPreference("Minimun coverage for a So So build", "<no limit>", "coverage",
				"warn"));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public List<Report> computeSimpleReport(BuildData data, Preferences prefs) {
		data = data.filter("Coverage").filter(5, "all");
		if (data.isEmpty())
			return Collections.emptyList();
		
		StringBuilder description = new StringBuilder();
		int defPercentage = -1;
		int defType = -1;
		
		List<String> prefered = new ArrayList<String>();
		prefered.add("line");
		prefered.add("instruction");
		
		List<Type> types = groupTypes(data.sumDistinct(3, 4));
		sort(types);
		for (Type type : types) {
			if (type.covered < 0 || type.total < 0)
				continue;
			
			int percentage = (int) round(100 * type.covered / type.total);
			
			if (description.length() > 0)
				description.append(", ");
			
			description.append(type.name).append(": ").append(percentage).append("%");
			
			if (showDetailsInDescription)
				description.append(" (").append(format(type.covered)).append("/").append(format1000(type.total, ""))
						.append(")");
			
			int typeIndex = prefered.indexOf(type.name);
			if (defType < 0 || defType < typeIndex) {
				defPercentage = percentage;
				defType = typeIndex;
			}
		}
		
		if (defPercentage < 0)
			return Collections.emptyList();
		
		prefs = prefs.child("coverage");
		int good = prefs.get("good", 0);
		int warn = prefs.get("warn", 0);
		
		return asList(new Report(computeStatus(defPercentage, good, warn), getName(), defPercentage + "%",
				description.toString()));
	}
	
	private BuildStatus computeStatus(int percentage, int good, int warn) {
		if (percentage < warn)
			return BuildStatus.Problematic;
		if (percentage < good)
			return BuildStatus.SoSo;
		return BuildStatus.Good;
	}
	
	private String format(double val) {
		return NumbersFormater.format1000(val, "");
	}
	
	private void sort(List<Type> types) {
		final Map<String, Integer> fixed = new HashMap<String, Integer>();
		fixed.put("class", 0);
		fixed.put("method", 1);
		fixed.put("block", 2);
		fixed.put("line", 3);
		fixed.put("branch", 4);
		fixed.put("instruction", 5);
		
		Collections.sort(types, new Comparator<Type>() {
			@Override
			public int compare(Type o1, Type o2) {
				int t1 = firstNonNull(fixed.get(o1.name), fixed.size());
				int t2 = firstNonNull(fixed.get(o2.name), fixed.size());
				if (t1 != t2)
					return t1 - t2;
				
				return o1.name.compareTo(o2.name);
			}
		});
	}
	
	private List<Type> groupTypes(Map<String[], Value> sums) {
		Map<String, Type> types = new HashMap<String, Type>();
		
		for (Map.Entry<String[], Value> entry : sums.entrySet()) {
			String[] keys = entry.getKey();
			double value = entry.getValue().value;
			
			Type type = types.get(keys[0]);
			if (type == null) {
				type = new Type(keys[0]);
				types.put(keys[0], type);
			}
			
			if ("total".equals(keys[1])) {
				type.total = value;
				
			} else if ("covered".equals(keys[1])) {
				type.covered = value;
			}
		}
		
		return new ArrayList<Type>(types.values());
	}
	
	private static class Type {
		final String name;
		double covered = -1;
		double total = -1;
		
		Type(String name) {
			this.name = name;
		}
	}
}
