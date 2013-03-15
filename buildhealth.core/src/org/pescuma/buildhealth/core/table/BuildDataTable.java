package org.pescuma.buildhealth.core.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
			String[] info = line.getInfo();
			if (column < info.length)
				result.add(info[column]);
			else
				result.add("");
		}
		return Collections.unmodifiableCollection(result);
	}
	
	@Override
	public void add(double value, String... info) {
		lines.add(new LineImpl(value, info));
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
		
		public String[] getInfo() {
			return info;
		}
		
		@Override
		public String toString() {
			return "LineImpl [" + value + " " + Arrays.toString(info) + "]";
		}
	}
}
