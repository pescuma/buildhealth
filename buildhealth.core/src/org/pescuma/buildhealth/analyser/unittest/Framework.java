package org.pescuma.buildhealth.analyser.unittest;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

class Framework {
	
	final String name;
	private final Map<String, Suite> suites = new TreeMap<String, Suite>(String.CASE_INSENSITIVE_ORDER);
	private Stats stats;
	
	Framework(String name) {
		this.name = name;
	}
	
	public Collection<Suite> getSuites() {
		return suites.values();
	}
	
	Suite getSuite(String name) {
		Suite result = suites.get(name);
		if (result == null) {
			result = new Suite(name);
			suites.put(name, result);
		}
		return result;
	}
	
	Stats getStats() {
		if (stats == null) {
			stats = new Stats();
			for (Suite child : suites.values())
				stats.add(child.getStats());
		}
		return stats;
	}
	
	void visit(TreeVisitor visitor) {
		visitor.visitFramework(this);
		
		for (Suite child : getSuites())
			child.visit(visitor);
	}
}
