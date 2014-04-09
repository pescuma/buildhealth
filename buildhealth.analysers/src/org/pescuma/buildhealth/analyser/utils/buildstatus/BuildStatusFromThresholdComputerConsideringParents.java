package org.pescuma.buildhealth.analyser.utils.buildstatus;

import java.util.ArrayList;
import java.util.List;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.prefs.Preferences;

public class BuildStatusFromThresholdComputerConsideringParents {
	
	private final BuildStatusFromThresholdComputer computer;
	
	public BuildStatusFromThresholdComputerConsideringParents(BuildStatusMessageFormater formater) {
		computer = new BuildStatusFromThresholdComputer(false, formater);
	}
	
	public BuildStatusAndExplanation computeParent(double total, BuildStatus childrenStatus, Preferences prefs,
			String[] keys, String... requiredSuffixKeys) {
		prefs = computer.findPrefs(prefs, keys, requiredSuffixKeys);
		
		BuildStatusThresholds thresholds = computer.findThresholds(prefs);
		if (thresholds == null)
			return null;
		
		if (childrenStatus == BuildStatus.Problematic && reportChildrenInsteadOfParent(thresholds))
			return null;
		
		BuildStatus status = computer.computeStatus(total, thresholds);
		String message = computer.computeMessage(status, thresholds, prefs.getCurrentKey());
		return new BuildStatusAndExplanation(status, message);
	}
	
	private boolean reportChildrenInsteadOfParent(BuildStatusThresholds thresholds) {
		return (int) thresholds.warn == 0;
	}
	
	public BuildStatusAndExplanation computeChild(double total, Preferences prefs, String[] keys,
			String... requiredSuffixKeys) {
		Preferences thisPrefs = computer.findPrefs(prefs, keys, requiredSuffixKeys);
		
		BuildStatusAndExplanation result = compute(total, thisPrefs);
		if (result != null)
			return result;
		
		if ((int) total == 0)
			return null;
		
		Preferences parentPrefs = findParentPrefs(prefs, keys, requiredSuffixKeys);
		if (parentPrefs == null)
			return null;
		
		return compute(total, parentPrefs);
	}
	
	private BuildStatusAndExplanation compute(double total, Preferences prefs) {
		BuildStatusThresholds thresholds = computer.findThresholds(prefs);
		if (thresholds == null)
			return null;
		
		BuildStatus status = computer.computeStatus(total, thresholds);
		String message = computer.computeMessage(status, thresholds, prefs.getCurrentKey());
		return new BuildStatusAndExplanation(status, message);
	}
	
	private Preferences findParentPrefs(Preferences prefs, String[] keys, String[] requiredSuffixKeys) {
		List<Preferences> result = new ArrayList<Preferences>();
		
		findPreferences(result, prefs, keys, requiredSuffixKeys, 0);
		
		for (Preferences candidate : result) {
			BuildStatusThresholds thresholds = computer.findThresholds(candidate);
			if (thresholds != null && reportChildrenInsteadOfParent(thresholds))
				return candidate;
		}
		
		return null;
	}
	
	private void findPreferences(List<Preferences> result, Preferences prefs, String[] keys,
			String[] requiredSuffixKeys, int index) {
		
		if (index < keys.length) {
			if (prefs.hasChild(keys[index]))
				findPreferences(result, prefs.child(keys[index]), keys, requiredSuffixKeys, index + 1);
			
			if (prefs.hasChild("*"))
				findPreferences(result, prefs.child("*"), keys, requiredSuffixKeys, index + 1);
		}
		
		for (String key : requiredSuffixKeys) {
			if (!prefs.hasChild(key))
				return;
			
			prefs = prefs.child(key);
		}
		
		result.add(prefs);
	}
}
