package org.pescuma.buildhealth.analyser;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.prefs.Preferences;

public class BuildStatusHelper {
	
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
	
	public static String[] splitCategory(String category) {
		String regex = "[/\\.|:]";
		category = category.replaceAll("^" + regex + "+", "");
		category = category.replaceAll(regex + "+$", "");
		if (category.isEmpty())
			return new String[0];
		return category.split(regex);
	}
	
}
