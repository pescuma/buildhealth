package org.pescuma.buildhealth.prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	
	@Override
	public List<String[]> getKeys(String... key) {
		List<String[]> result = new ArrayList<String[]>();
		
		for (Iterator<String[]> it = data.keySet().iterator(); it.hasNext();) {
			String[] candidate = it.next();
			if (startsWith(candidate, key))
				result.add(candidate);
		}
		
		return Collections.unmodifiableList(result);
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
