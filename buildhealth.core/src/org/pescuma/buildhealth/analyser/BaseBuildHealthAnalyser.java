package org.pescuma.buildhealth.analyser;

import java.util.Collections;
import java.util.List;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.prefs.Preferences;

public abstract class BaseBuildHealthAnalyser implements BuildHealthAnalyser {
	
	@Override
	public String getName() {
		return null;
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public List<BuildHealthAnalyserPreference> getPreferences() {
		return Collections.emptyList();
	}
	
	@Override
	public List<Report> computeReport(BuildData data, Preferences prefs, int opts) {
		return null;
	}
	
}
