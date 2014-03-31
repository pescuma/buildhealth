package org.pescuma.buildhealth.analyser.utils;

import static org.pescuma.buildhealth.analyser.utils.NumbersFormater.*;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.prefs.Preferences;

public class BuildStatusFromThresholdComputer {
	
	private final boolean biggerIsBetter;
	
	public BuildStatusFromThresholdComputer(boolean biggerIsBetter) {
		this.biggerIsBetter = biggerIsBetter;
	}
	
	public BuildStatusAndExplanation compute(double total, Preferences prefs, String[] startKeys, String... moreKeys) {
		String[] keys = ArrayUtils.addAll(startKeys, moreKeys);
		
		prefs = findPrefs(prefs, keys);
		
		if (prefs.get("good", null) == null && prefs.get("warn", null) == null)
			return null;
		
		double defVal = biggerIsBetter ? 0.0 : Double.MAX_VALUE;
		double good = prefs.get("good", defVal);
		double warn = prefs.get("warn", defVal);
		
		BuildStatus status = computeStatus(total, good, warn);
		
		String message;
		if (status == BuildStatus.Good)
			message = null;
		else
			message = computeMessage(status, good, warn, prefs.getCurrentKey());
		
		return new BuildStatusAndExplanation(status, message);
	}
	
	private Preferences findPrefs(Preferences prefs, String[] keys) {
		for (String key : keys) {
			if (prefs.hasChild(key))
				prefs = prefs.child(key);
			
			else if (prefs.hasChild("*"))
				prefs = prefs.child("*");
			
			else
				return prefs.child(key);
		}
		
		return prefs;
	}
	
	private BuildStatus computeStatus(double total, double good, double warn) {
		if (biggerIsBetter) {
			if (total < warn)
				return BuildStatus.Problematic;
			if (total < good)
				return BuildStatus.SoSo;
			return BuildStatus.Good;
			
		} else {
			if (total > warn)
				return BuildStatus.Problematic;
			if (total > good)
				return BuildStatus.SoSo;
			return BuildStatus.Good;
		}
	}
	
	private String computeMessage(BuildStatus status, double good, double warn, String[] prefKey) {
		switch (status) {
			case SoSo:
				return computeSoSoMessage(good, prefKey);
			case Problematic:
				return computeProblematicMessage(warn, prefKey);
			default:
				throw new IllegalArgumentException();
		}
	}
	
	protected String computeSoSoMessage(double good, String[] prefKey) {
		return "Unstable if " + (biggerIsBetter ? "less" : "more") + " than " + formatValue(good);
	}
	
	protected String computeProblematicMessage(double warn, String[] prefKey) {
		return "Should not be " + (biggerIsBetter ? "less" : "more") + " than " + formatValue(warn);
	}
	
	protected String formatValue(double value) {
		return format1000(value);
	}
	
	protected String getPrefKeyDetails(Collection<String> prefKey) {
		if (prefKey.size() < 1)
			return "";
		
		Deque<String> pieces = new LinkedList<String>(prefKey);
		pieces.removeFirst();
		
		StringBuilder result = new StringBuilder();
		
		if (!pieces.isEmpty()) {
			String val = pieces.removeFirst();
			if (!val.equals("*"))
				result.append(" in ").append(val);
		}
		
		if (!pieces.isEmpty()) {
			String val = pieces.removeFirst();
			if (!val.equals("*"))
				result.append(" measured by ").append(val);
		}
		
		if (!pieces.isEmpty())
			result.append(" in ").append(StringUtils.join(pieces, "."));
		
		return result.toString();
	}
}
