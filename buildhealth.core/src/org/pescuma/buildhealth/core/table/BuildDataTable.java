package org.pescuma.buildhealth.core.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pescuma.buildhealth.core.BuildData;

public class BuildDataTable implements BuildData {
	
	private final List<LineImpl> lines;
	
	public BuildDataTable() {
		lines = new ArrayList<LineImpl>();
	}
	
	private BuildDataTable(List<LineImpl> lines) {
		this.lines = lines;
	}
	
	@Override
	public boolean isEmpty() {
		return lines.isEmpty();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Line> getLines() {
		return (List) Collections.unmodifiableList(lines);
	}
	
	@Override
	public void add(double value, String... info) {
		lines.add(new LineImpl(value, info));
	}
	
	@Override
	public BuildData filter(String... info) {
		List<LineImpl> filtered = new ArrayList<LineImpl>();
		for (LineImpl line : lines)
			if (line.infoStartsWith(info))
				filtered.add(line);
		
		return new BuildDataTable(filtered);
	}
	
	@Override
	public double sum() {
		double result = 0;
		for (Line line : lines)
			result += line.getValue();
		return result;
	}
	
	private static class LineImpl implements Line {
		
		final double value;
		final String[] info;
		
		LineImpl(double value, String[] info) {
			this.value = value;
			this.info = info;
		}
		
		boolean infoStartsWith(String[] start) {
			if (start.length > info.length)
				return false;
			
			for (int i = 0; i < start.length; i++)
				if (!info[i].equals(start[i]))
					return false;
			
			return true;
		}
		
		public double getValue() {
			return value;
		}
		
		public String[] getInfo() {
			return info;
		}
	}
}
