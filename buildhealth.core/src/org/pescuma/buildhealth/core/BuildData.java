package org.pescuma.buildhealth.core;

import java.util.Collection;
import java.util.Map;

public interface BuildData {
	
	boolean isEmpty();
	
	void add(double value, String... info);
	
	double get(String... info);
	
	Collection<Line> getLines();
	
	Collection<String> getDistinct(int column);
	
	Map<String, Value> sumDistinct(int columns);
	
	Map<String[], Value> sumDistinct(int... columns);
	
	BuildData filter(String... info);
	
	BuildData filter(int column, String name);
	
	double sum();
	
	int size();
	
	public interface Line {
		double getValue();
		
		String getColumn(int column);
		
		/** @param column null or empty to get all */
		String[] getColumns(int... column);
	}
	
	public static class Value {
		public double value;
	}
}
