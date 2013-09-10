package org.pescuma.buildhealth.core;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Predicate;

public interface BuildData {
	
	boolean isEmpty();
	
	void add(double value, String... info);
	
	double get(String... info);
	
	Collection<Line> getLines();
	
	Collection<String> getDistinct(int column);
	
	Collection<String[]> getDistinct(int... columns);
	
	Map<String, Value> sumDistinct(int columns);
	
	Map<String[], Value> sumDistinct(int... columns);
	
	BuildData filter(String... info);
	
	BuildData filter(int column, String value);
	
	BuildData filter(Predicate<Line> predicate);
	
	BuildData filter(int column, Predicate<String> predicate);
	
	double sum();
	
	int size();
	
	Collection<String> getColumn(int column);
	
	/** @param column null or empty to get all */
	Collection<String[]> getColumns(int... columns);
	
	public interface Line {
		double getValue();
		
		String getColumn(int column);
		
		/** @param column null or empty to get all */
		String[] getColumns(int... columns);
	}
	
	public static class Value {
		public double value;
		
		@Override
		public String toString() {
			return Double.toString(value);
		}
	}
}
