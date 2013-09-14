package org.pescuma.buildhealth.notifiers;

import java.util.List;

import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.core.prefs.BuildHealthPreference;
import org.pescuma.buildhealth.prefs.Preferences;

public interface BuildHealthNotifier {
	
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
	List<BuildHealthPreference> getPreferences();
	
	void sendNotification(Report report, Preferences prefs, BuildHealthNotifierTracker tracker);
	
}
