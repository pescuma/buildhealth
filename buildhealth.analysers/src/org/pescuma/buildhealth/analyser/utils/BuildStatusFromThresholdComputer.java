package org.pescuma.buildhealth.analyser.utils;

import static org.pescuma.buildhealth.analyser.utils.NumbersFormater.*;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.prefs.Preferences;

public class BuildStatusFromThresholdComputer {
	
	private final boolean biggerIsBetter;
	
	public BuildStatusFromThresholdComputer(boolean biggerIsBetter) {
		this.biggerIsBetter = biggerIsBetter;
	}
	
	public BuildStatusAndExplanation compute(double total, Preferences prefs) {
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
	
}
