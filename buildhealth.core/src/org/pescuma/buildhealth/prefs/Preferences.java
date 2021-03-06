package org.pescuma.buildhealth.prefs;

import java.util.Collection;
import java.util.List;

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
	
	public void set(String key, String value) {
		if (Strings.isNullOrEmpty(value))
			store.remove(subkey(key));
		else
			store.put(value, subkey(key));
	}
	
	public int get(String key, int defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return Integer.parseInt(result);
	}
	
	public void set(String key, int value) {
		store.put(Integer.toString(value), subkey(key));
	}
	
	public long get(String key, long defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return Long.parseLong(result);
	}
	
	public void set(String key, long value) {
		store.put(Long.toString(value), subkey(key));
	}
	
	public float get(String key, float defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return Float.parseFloat(result);
	}
	
	public void set(String key, float value) {
		store.put(Float.toString(value), subkey(key));
	}
	
	public double get(String key, double defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return Double.parseDouble(result);
	}
	
	public void set(String key, double value) {
		store.put(Double.toString(value), subkey(key));
	}
	
	public boolean get(String key, boolean defVal) {
		String result = store.get(subkey(key));
		if (result == null)
			return defVal;
		return Boolean.parseBoolean(result);
	}
	
	public void set(String key, boolean value) {
		store.put(Boolean.toString(value), subkey(key));
	}
	
	public Preferences child(String... key) {
		if (key == null)
			throw new IllegalArgumentException();
		if (key.length < 1)
			return this;
		
		for (int i = 0; i < key.length; i++)
			if (Strings.isNullOrEmpty(key[i]))
				throw new IllegalArgumentException();
		
		return new Preferences(store, subkey(key));
	}
	
	public boolean hasChild(String key) {
		return !store.getKeys(subkey(key)).isEmpty();
	}
	
	private String[] subkey(String... second) {
		return ArrayUtils.addAll(currentKey, second);
	}
	
	public List<String[]> getKeys() {
		return store.getKeys(currentKey);
	}
	
	public String[] getCurrentKey() {
		return currentKey;
	}
	
	public Collection<String> getChildrenKeys() {
		return store.getChildrenKeys(currentKey);
	}
	
}
