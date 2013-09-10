package org.pescuma.buildhealth.analyser.unittest;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

class Language {
	
	final String name;
	private final Map<String, Framework> frameworks = new TreeMap<String, Framework>(String.CASE_INSENSITIVE_ORDER);
	private Stats stats;
	
	Language(String name) {
		this.name = name;
	}
	
	public Collection<Framework> getFrameworks() {
		return frameworks.values();
	}
	
	Framework getFramework(String name) {
		Framework result = frameworks.get(name);
		if (result == null) {
			result = new Framework(name);
			frameworks.put(name, result);
		}
		return result;
	}
	
	Stats getStats() {
		if (stats == null) {
			stats = new Stats();
			for (Framework child : frameworks.values())
				stats.add(child.getStats());
		}
		return stats;
	}
	
	void visit(TreeVisitor visitor) {
		visitor.visitLanguage(this);
		
		for (Framework child : getFrameworks())
			child.visit(visitor);
	}
}
