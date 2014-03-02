package org.pescuma.buildhealth.utils;

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
	
	public Location() {
		this(null, NO_INFO, NO_INFO, NO_INFO, NO_INFO, true);
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
	
}
