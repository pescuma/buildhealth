package org.pescuma.buildhealth.core;

import java.util.List;

public interface BuildData {
	
	boolean isEmpty();
	
	void add(double value, String... info);
	
	List<Line> getLines();
	
	BuildData filter(String... info);
	
	double sum();
	
	public interface Line {
		double getValue();
		
		String[] getInfo();
	}
}
