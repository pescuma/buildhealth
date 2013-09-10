package org.pescuma.buildhealth.analyser.unittest;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

class Suite {
	
	final String name;
	final Stats statsWithoutTests = new Stats();
	private final Map<String, Test> tests = new TreeMap<String, Test>(String.CASE_INSENSITIVE_ORDER);
	private Stats stats;
	
	Suite(String name) {
		this.name = name;
	}
	
	public Collection<Test> getTests() {
		return tests.values();
	}
	
	Test getTest(String name) {
		Test result = tests.get(name);
		if (result == null) {
			result = new Test(name);
			tests.put(name, result);
		}
		return result;
	}
	
	Stats getStats() {
		if (stats == null) {
			stats = new Stats();
			stats.add(statsWithoutTests);
			for (Test child : tests.values())
				stats.add(child.getStats());
		}
		return stats;
	}
	
	void visit(TreeVisitor visitor) {
		visitor.visitSuite(this);
		
		for (Test child : getTests())
			child.visit(visitor);
	}
}
