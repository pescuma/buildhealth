package org.pescuma.buildhealth.analyser.unittest;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

class Tree {
	
	private final Map<String, Language> languages = new TreeMap<String, Language>(String.CASE_INSENSITIVE_ORDER);
	private Stats stats;
	
	public Collection<Language> getLanguages() {
		return languages.values();
	}
	
	Language getLanguage(String name) {
		Language result = languages.get(name);
		if (result == null) {
			result = new Language(name);
			languages.put(name, result);
		}
		return result;
	}
	
	Stats getStats() {
		if (stats == null) {
			stats = new Stats();
			for (Language child : languages.values())
				stats.add(child.getStats());
		}
		return stats;
	}
	
	void visit(TreeVisitor visitor) {
		for (Language child : getLanguages())
			child.visit(visitor);
	}
}
