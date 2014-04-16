package org.pescuma.buildhealth.analyser;

import java.util.List;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.projects.Projects;

public interface BuildHealthAnalyser {
	
	/**
	 * @return Can't be null
	 */
	String getName();
	
	/**
	 * The smaller ones go first
	 */
	int getPriority();
	
	/**
	 * @return Can't be null
	 */
	List<BuildHealthPreference> getKnownPreferences();
	
	/**
	 * @param projects TODO
	 * @param opts Flags from BuildHealth.ReportFlags
	 */
	List<Report> computeReport(BuildData data, Projects projects, Preferences prefs, int opts);
}
