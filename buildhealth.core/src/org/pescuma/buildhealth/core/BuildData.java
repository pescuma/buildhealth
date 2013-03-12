package org.pescuma.buildhealth.core;

import java.util.Collection;

public interface BuildData {
	
	boolean isEmpty();
	
	void add(double value, String... info);
	
	Collection<Line> getLines();
	
	BuildData filter(String... info);
	
	BuildData filter(int column, String name);
	
	double sum();
	
	int size();
	
	public interface Line {
		double getValue();
		
		String[] getInfo();
	}
	
}
