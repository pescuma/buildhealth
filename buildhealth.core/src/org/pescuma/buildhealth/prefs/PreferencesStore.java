package org.pescuma.buildhealth.prefs;

import java.util.List;

public interface PreferencesStore {
	
	void put(String value, String... key);
	
	String get(String... key);
	
	void remove(String... key);
	
	void removeChildren(String... key);
	
	List<String[]> getKeys(String... key);
	
}
