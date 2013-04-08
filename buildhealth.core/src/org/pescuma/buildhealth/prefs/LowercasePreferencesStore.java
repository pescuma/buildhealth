package org.pescuma.buildhealth.prefs;

import java.util.Arrays;
import java.util.List;

public class LowercasePreferencesStore implements PreferencesStore {
	
	protected final PreferencesStore next;
	
	public LowercasePreferencesStore(PreferencesStore next) {
		this.next = next;
	}
	
	private String[] toLower(String[] key) {
		String[] result = Arrays.copyOf(key, key.length);
		for (int i = 0; i < result.length; i++)
			result[i] = result[i].toLowerCase();
		return result;
	}
	
	@Override
	public void put(String value, String... key) {
		next.put(value, toLower(key));
	}
	
	@Override
	public String get(String... key) {
		return next.get(toLower(key));
	}
	
	@Override
	public void remove(String... key) {
		next.remove(toLower(key));
	}
	
	@Override
	public void removeChildren(String... key) {
		next.removeChildren(toLower(key));
	}
	
	@Override
	public List<String[]> getKeys(String... key) {
		return next.getKeys(toLower(key));
	}
	
}
