package org.pescuma.buildhealth.analyser;

import java.util.List;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.prefs.Preferences;

public interface BuildHealthAnalyser {
	
	String getName();
	
	List<BuildHealthAnalyserPreference> getPreferences();
	
	List<Report> computeSimpleReport(BuildData data, Preferences prefs);
}
