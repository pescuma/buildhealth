package org.pescuma.buildhealth.prefs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MemoryPreferencesStore implements PreferencesStore {
	
	private final Map<String[], String> data = new HashMap<String[], String>();
	
	@Override
	public void put(String value, String... key) {
		data.put(key, value);
	}
	
	@Override
	public String get(String... key) {
		return data.get(key);
	}
	
	@Override
	public void remove(String... key) {
		data.remove(key);
	}
	
	@Override
	public void removeChildren(String... key) {
		for (Iterator<String[]> it = data.keySet().iterator(); it.hasNext();) {
			String[] candidate = it.next();
			if (startsWith(candidate, key))
				it.remove();
		}
	}
	
	private boolean startsWith(String[] full, String[] start) {
		if (start.length > full.length)
			return false;
		
		for (int i = 0; i < start.length; i++)
			if (!full[i].equals(start[i]))
				return false;
		
		return true;
	}
	
}
