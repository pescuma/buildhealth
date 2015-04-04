package org.pescuma.buildhealth.core;

import java.util.Collections;
import java.util.List;

import org.pescuma.buildhealth.analyser.BuildHealthAnalyser;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.projects.Projects;
import org.pescuma.datatable.DataTable;

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
	public List<BuildHealthPreference> getKnownPreferences() {
		return Collections.emptyList();
	}
	
	@Override
	public List<Report> computeReport(DataTable data, Projects projects, Preferences prefs, int opts) {
		return null;
	}
	
}
