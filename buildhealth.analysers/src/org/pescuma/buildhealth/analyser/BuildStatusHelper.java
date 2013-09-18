package org.pescuma.buildhealth.analyser;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.prefs.Preferences;

public class BuildStatusHelper {
	
	public static BuildStatus computeStatusFromThresholdIfExists(Preferences prefs, double total, boolean biggerIsBetter) {
		if (prefs.get("good", null) == null && prefs.get("warn", null) == null)
			return null;
		
		return computeStatusFromThreshold(prefs, total, biggerIsBetter);
	}
	
	public static BuildStatus computeStatusFromThreshold(Preferences prefs, double total, boolean biggerIsBetter) {
		double defVal = biggerIsBetter ? 0.0 : Double.MAX_VALUE;
		double good = prefs.get("good", defVal);
		double warn = prefs.get("warn", defVal);
		return computeStatusFromThreshold(total, good, warn, biggerIsBetter);
	}
	
	public static BuildStatus computeStatusFromThreshold(double percentage, double good, double warn,
			boolean biggerIsBetter) {
		if (biggerIsBetter) {
			if (percentage < warn)
				return BuildStatus.Problematic;
			if (percentage < good)
				return BuildStatus.SoSo;
			return BuildStatus.Good;
		} else {
			if (percentage > warn)
				return BuildStatus.Problematic;
			if (percentage > good)
				return BuildStatus.SoSo;
			return BuildStatus.Good;
		}
	}
	
}
