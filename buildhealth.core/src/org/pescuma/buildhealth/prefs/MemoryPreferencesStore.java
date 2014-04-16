package org.pescuma.buildhealth.prefs;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MemoryPreferencesStore implements PreferencesStore {
	
	private final Map<String[], String> data = new TreeMap<String[], String>(getKeyComparator());
	
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
	
	private static Comparator<? super String[]> getKeyComparator() {
		return new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				int size = min(o1.length, o2.length);
				for (int i = 0; i < size; i++) {
					int comp = o1[i].compareTo(o2[i]);
					if (comp != 0)
						return comp;
				}
				return o1.length - o2.length;
			}
		};
	}
	
	@Override
	public Collection<String> getChildrenKeys(String... key) {
		Set<String> result = new HashSet<String>();
		
		for (Iterator<String[]> it = data.keySet().iterator(); it.hasNext();) {
			String[] candidate = it.next();
			if (startsWith(candidate, key) && candidate.length > key.length)
				result.add(candidate[key.length]);
		}
		
		return Collections.unmodifiableSet(result);
	}
}
