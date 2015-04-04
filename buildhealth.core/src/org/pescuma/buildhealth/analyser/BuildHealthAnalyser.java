package org.pescuma.buildhealth.analyser;

import java.util.List;

import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;
import org.pescuma.buildhealth.projects.Projects;
import org.pescuma.datatable.DataTable;

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
	List<Report> computeReport(DataTable data, Projects projects, Preferences prefs, int opts);
}
