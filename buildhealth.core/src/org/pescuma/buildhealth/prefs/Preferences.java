package org.pescuma.buildhealth.prefs;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Strings;

public class Preferences {
	
	private final PreferencesStore store;
	private final String[] currentKey;
	
	public Preferences(PreferencesStore store) {
		this.store = store;
		this.currentKey = new String[0];
	}
	
	private Preferences(PreferencesStore store, String... key) {
		this.store = store;
		this.currentKey = key;
	}
	
	public String get(String key, String defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return result;
	}
	
	public int get(String key, int defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return Integer.parseInt(result);
	}
	
	public long get(String key, long defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return Long.parseLong(result);
	}
	
	public double get(String key, double defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return Double.parseDouble(result);
	}
	
	public float get(String key, float defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return Float.parseFloat(result);
	}
	
	public Preferences child(String key) {
		if (Strings.isNullOrEmpty(key))
			throw new IllegalArgumentException();
		
		return new Preferences(store, subkey(key));
	}
	
	private String[] subkey(String second) {
		return ArrayUtils.add(currentKey, second);
	}
	
}
