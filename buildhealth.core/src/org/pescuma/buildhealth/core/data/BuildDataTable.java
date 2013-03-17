package org.pescuma.buildhealth.core.data;

import static java.util.Arrays.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.pescuma.buildhealth.core.BuildData;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class BuildDataTable implements BuildData {
	
	private final Collection<LineImpl> lines;
	
	public BuildDataTable() {
		lines = new ArrayList<LineImpl>();
	}
	
	private BuildDataTable(Collection<LineImpl> lines) {
		this.lines = lines;
	}
	
	@Override
	public boolean isEmpty() {
		return lines.isEmpty();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<Line> getLines() {
		return (Collection) Collections.unmodifiableCollection(lines);
	}
	
	@Override
	public Collection<String> getDistinct(int column) {
		Set<String> result = new HashSet<String>();
		for (Line line : lines) {
			String info = line.getColumn(column);
			result.add(info);
		}
		return Collections.unmodifiableCollection(result);
	}
	
	@Override
	public Map<String[], Value> sumDistinct(int... columns) {
		// By default sort by the columns text
		Map<String[], Value> result = new TreeMap<String[], Value>(new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				for (int i = 0; i < o1.length; i++) {
					int comp = o1[i].compareTo(o2[i]);
					if (comp != 0)
						return comp;
				}
				return 0;
			}
		});
		
		for (Line line : lines) {
			String[] key = line.getColumns(columns);
			
			Value value = result.get(key);
			if (value == null) {
				value = new Value();
				result.put(key, value);
			}
			
			value.value += line.getValue();
		}
		
		return result;
	}
	
	@Override
	public Map<String, Value> sumDistinct(int columns) {
		// By default sort by the columns text
		Map<String, Value> result = new TreeMap<String, Value>();
		
		for (Line line : lines) {
			String key = line.getColumn(columns);
			
			Value value = result.get(key);
			if (value == null) {
				value = new Value();
				result.put(key, value);
			}
			
			value.value += line.getValue();
		}
		
		return result;
	}
	
	@Override
	public void add(double value, String... info) {
		info = removeEmptyAtEnd(info);
		lines.add(new LineImpl(value, info));
	}
	
	private String[] removeEmptyAtEnd(String[] info) {
		int last = info.length - 1;
		for (; last > 0 && info[last].isEmpty(); last--)
			;
		last++;
		if (last == info.length)
			return info;
		else
			return copyOf(info, last);
	}
	
	@Override
	public BuildData filter(final String... info) {
		Collection<LineImpl> filtered = Collections2.filter(lines, new Predicate<LineImpl>() {
			@Override
			public boolean apply(LineImpl input) {
				return input.infoStartsWith(info);
			}
		});
		return new BuildDataTable(filtered);
	}
	
	@Override
	public BuildData filter(final int column, final String name) {
		Collection<LineImpl> filtered = Collections2.filter(lines, new Predicate<LineImpl>() {
			@Override
			public boolean apply(LineImpl input) {
				return input.hasInfo(column, name);
			}
		});
		return new BuildDataTable(filtered);
	}
	
	@Override
	public double sum() {
		double result = 0;
		for (Line line : lines)
			result += line.getValue();
		return result;
	}
	
	@Override
	public int size() {
		return lines.size();
	}
	
	private static class LineImpl implements Line {
		
		final double value;
		final String[] info;
		
		LineImpl(double value, String[] info) {
			this.value = value;
			this.info = info;
		}
		
		boolean hasInfo(int column, String name) {
			if (column >= info.length)
				return name.isEmpty();
			
			return info[column].equals(name);
		}
		
		boolean infoStartsWith(String[] start) {
			for (int i = 0; i < start.length; i++) {
				String val = (i < info.length ? info[i] : "");
				if (!val.equals(start[i]))
					return false;
			}
			
			return true;
		}
		
		public double getValue() {
			return value;
		}
		
		public String getColumn(int column) {
			if (column < info.length)
				return info[column];
			else
				return "";
		}
		
		@Override
		public String[] getColumns(int... columns) {
			if (columns == null || columns.length == 0)
				return info;
			
			String[] result = new String[columns.length];
			for (int i = 0; i < columns.length; i++)
				result[i] = getColumn(columns[i]);
			return result;
		}
		
		@Override
		public String toString() {
			return "LineImpl [" + value + " " + Arrays.toString(info) + "]";
		}
	}
}
