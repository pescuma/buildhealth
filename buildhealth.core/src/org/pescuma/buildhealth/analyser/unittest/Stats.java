package org.pescuma.buildhealth.analyser.unittest;

import static java.lang.Math.*;
import static org.pescuma.buildhealth.analyser.NumbersFormater.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Value;
import org.pescuma.buildhealth.core.BuildStatus;

class Stats {
	Map<String, Value> types;
	StringBuilder message = new StringBuilder();
	
	Stats() {
		types = new HashMap<String, Value>();
	}
	
	Stats(BuildData data) {
		types = data.sumDistinct(UnitTestAnalyser.COLUMN_TYPE);
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
		types.clear();
		types.putAll(other.types);
		
		message.setLength(0);
		message.append(other.message);
		
	}
}
