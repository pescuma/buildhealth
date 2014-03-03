package org.pescuma.buildhealth.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

public class Location {
	
	public static final int NO_INFO = -1;
	
	public final String file;
	public final int beginLine;
	public final int beginColumn;
	public final int endLine;
	public final int endColumn;
	
	/**
	 * @param dummy Just to create a different signature to the private method
	 */
	private Location(String file, int beginLine, int beginColumn, int endLine, int endColumn, boolean dummy) {
		this.file = file;
		this.beginLine = beginLine;
		this.beginColumn = beginColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
	}
	
	public Location(String file) {
		this(file, NO_INFO, NO_INFO, NO_INFO, NO_INFO, true);
		
		Validate.notNull(file);
	}
	
	public Location(String file, int line) {
		this(file, line, NO_INFO, line, NO_INFO, true);
		
		Validate.notNull(file);
		Validate.isTrue(line > 0);
	}
	
	public Location(String file, int line, int column) {
		this(file, line, column, line, column, true);
		
		Validate.notNull(file);
		Validate.isTrue(line > 0);
		Validate.isTrue(column > 0);
	}
	
	public Location(String file, int beginLine, int beginColumn, int endLine, int endColumn) {
		this(file, beginLine, beginColumn, endLine, endColumn, true);
		
		Validate.notNull(file);
		Validate.isTrue(beginLine > 0);
		Validate.isTrue(beginColumn > 0);
		Validate.isTrue(endLine > 0);
		Validate.isTrue(endColumn > 0);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + beginColumn;
		result = prime * result + beginLine;
		result = prime * result + endColumn;
		result = prime * result + endLine;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (beginColumn != other.beginColumn)
			return false;
		if (beginLine != other.beginLine)
			return false;
		if (endColumn != other.endColumn)
			return false;
		if (endLine != other.endLine)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		if (file == null)
			return "";
		
		StringBuilder result = new StringBuilder();
		
		result.append(file);
		
		if (beginLine == NO_INFO)
			return result.toString();
		
		result.append(">").append(beginLine);
		
		if (beginColumn == NO_INFO)
			return result.toString();
		
		result.append(":").append(beginColumn);
		
		if (endLine != beginLine || endColumn != beginColumn)
			result.append(":").append(endLine).append(":").append(endColumn);
		
		return result.toString();
	}
	
	public static String toFormatedString(Location location) {
		if (location == null)
			return "";
		
		return location.toString();
	}
	
	public static String toFormatedString(List<Location> locations) {
		if (locations == null || locations.size() < 1)
			return "";
		
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Location loc : locations) {
			if (first)
				first = false;
			else
				result.append("|");
			
			result.append(loc.toString());
		}
		return result.toString();
	}
	
	public static List<Location> parse(String column) {
		List<Location> result = new ArrayList<Location>();
		
		for (String loc : column.split("\\|")) {
			Location location = parseLocation(loc);
			if (location != null)
				result.add(location);
		}
		
		return result;
	}
	
	private static Location parseLocation(String loc) {
		String[] places = loc.split(">");
		
		if (places.length < 1)
			return null;
		
		if (places.length > 2)
			throw new IllegalStateException(loc + " is not a location");
		
		String file = places[0];
		if (places.length == 1)
			return new Location(file);
		
		String[] lines = places[1].split(":");
		if (lines.length < 1)
			return new Location(file);
		else if (lines.length == 1)
			return new Location(file, Integer.parseInt(lines[0]));
		else if (lines.length == 2)
			return new Location(file, Integer.parseInt(lines[0]), Integer.parseInt(lines[1]));
		else if (lines.length == 4)
			return new Location(file, Integer.parseInt(lines[0]), Integer.parseInt(lines[1]),
					Integer.parseInt(lines[2]), Integer.parseInt(lines[3]));
		else
			throw new IllegalStateException(loc + " is not a location");
	}
	
	public static Location create(String file, String line) {
		return create(file, line, null, null, null);
	}
	
	public static Location create(String file, String line, String column) {
		return create(file, line, column, null, null);
	}
	
	public static Location create(String file, String beginLine, String beginColumn, String endLine, String endColumn) {
		return create(file, toInt(beginLine), toInt(beginColumn), toInt(endLine), toInt(endColumn));
	}
	
	private static Integer toInt(String line) {
		if (line == null || line.trim().isEmpty())
			return null;
		else
			return Integer.parseInt(line);
	}
	
	public static Location create(String file, Integer beginLine, Integer beginColumn, Integer endLine,
			Integer endColumn) {
		if (file == null)
			return null;
		
		beginLine = nullIfIncorrect(beginLine);
		beginColumn = nullIfIncorrect(beginColumn);
		endLine = nullIfIncorrect(endLine);
		endColumn = nullIfIncorrect(endColumn);
		
		if (beginLine == null && endLine != null)
			beginLine = 1;
		
		if (beginLine == null)
			return new Location(file);
		
		if (beginLine != null && beginColumn == null && endLine == null && endColumn == null)
			return new Location(file, beginLine);
		
		if (beginLine != null && beginColumn == null && endLine != null && endColumn == null
				&& beginLine.equals(endLine))
			return new Location(file, beginLine);
		
		if (beginLine != null && beginColumn != null && endLine == null && endColumn == null)
			return new Location(file, beginLine, beginColumn);
		
		if (endColumn == null)
			endColumn = beginColumn;
		if (endLine == null)
			endLine = beginLine;
		
		if (beginColumn == null && endColumn != null)
			beginColumn = 1;
		
		if (beginColumn == null && endColumn == null) {
			beginColumn = 1;
			endColumn = 9999;
		}
		
		return new Location(file, beginLine, beginColumn, endLine, endColumn);
	}
	
	private static Integer nullIfIncorrect(Integer val) {
		if (val != null && val < 1)
			return null;
		else
			return val;
	}
}
