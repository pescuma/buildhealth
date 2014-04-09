package org.pescuma.buildhealth.analyser.utils.buildstatus;

import org.apache.commons.lang3.ArrayUtils;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.prefs.Preferences;

public class BuildStatusFromThresholdComputer {
	
	private final boolean biggerIsBetter;
	private final BuildStatusMessageFormater formater;
	
	public BuildStatusFromThresholdComputer(boolean biggerIsBetter, BuildStatusMessageFormater formater) {
		this.biggerIsBetter = biggerIsBetter;
		this.formater = formater;
	}
	
	public BuildStatusAndExplanation compute(double total, Preferences prefs, String[] startKeys, String... moreKeys) {
		prefs = findPrefs(prefs, startKeys, moreKeys);
		
		BuildStatusThresholds thresholds = findThresholds(prefs);
		if (thresholds == null)
			return null;
		
		BuildStatus status = computeStatus(total, thresholds);
		String message = computeMessage(status, thresholds, prefs.getCurrentKey());
		return new BuildStatusAndExplanation(status, message);
	}
	
	Preferences findPrefs(Preferences prefs, String[] startKeys, String... moreKeys) {
		String[] keys = ArrayUtils.addAll(startKeys, moreKeys);
		
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
	
	BuildStatusThresholds findThresholds(Preferences prefs) {
		if (prefs.get("good", null) == null && prefs.get("warn", null) == null)
			return null;
		
		double defVal = biggerIsBetter ? 0.0 : Double.MAX_VALUE;
		double good = prefs.get("good", defVal);
		double warn = prefs.get("warn", defVal);
		
		return new BuildStatusThresholds(good, warn);
	}
	
	BuildStatus computeStatus(double total, BuildStatusThresholds thresholds) {
		if (biggerIsBetter) {
			if (total < thresholds.warn)
				return BuildStatus.Problematic;
			if (total < thresholds.good)
				return BuildStatus.SoSo;
			return BuildStatus.Good;
			
		} else {
			if (total > thresholds.warn)
				return BuildStatus.Problematic;
			if (total > thresholds.good)
				return BuildStatus.SoSo;
			return BuildStatus.Good;
		}
	}
	
	String computeMessage(BuildStatus status, BuildStatusThresholds thresholds, String[] prefKey) {
		switch (status) {
			case Good:
				return null;
			case SoSo:
				return formater.computeSoSoMessage(thresholds.good, prefKey);
			case Problematic:
				return formater.computeProblematicMessage(thresholds.warn, prefKey);
			default:
				throw new IllegalArgumentException();
		}
	}
}
