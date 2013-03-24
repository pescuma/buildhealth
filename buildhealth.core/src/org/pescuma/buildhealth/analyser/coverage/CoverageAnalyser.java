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

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.analyser.NumbersFormater;
import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Value;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Coverage,language,framework,{type:line,block,method,class},{covered,total},{place type:all,file,package,class,method},place
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
 * 10 | Coverage,java,emma,line,covered,all
 * 15 | Coverage,java,emma,line,total,all
 * 1 | Coverage,java,emma,line,covered,class,a,b,c,D
 * 2 | Coverage,java,emma,line,total,class,a,b,c,D
 * </pre>
 */
public class CoverageAnalyser implements BuildHealthAnalyser {
	
	private final boolean showDetailsInDescription = false;
	
	@Override
	public List<Report> computeSimpleReport(BuildData data) {
		data = data.filter("Coverage").filter(5, "all");
		if (data.isEmpty())
			return Collections.emptyList();
		
		StringBuilder description = new StringBuilder();
		int defPercentage = -1;
		
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
			
			if (defPercentage < 0 || "line".equals(type.name))
				defPercentage = percentage;
		}
		
		if (defPercentage < 0)
			return Collections.emptyList();
		
		return asList(new Report(BuildStatus.Good, "Coverage", defPercentage + "%", description.toString()));
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