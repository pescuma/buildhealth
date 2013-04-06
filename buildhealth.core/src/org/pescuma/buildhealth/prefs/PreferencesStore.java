package org.pescuma.buildhealth.prefs;

public interface PreferencesStore {
	
	void put(String value, String... key);
	
	String get(String... key);
	
	void remove(String... key);
	
	void removeChildren(String... key);
	
}
