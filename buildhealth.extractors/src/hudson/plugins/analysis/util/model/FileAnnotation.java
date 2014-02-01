package hudson.plugins.analysis.util.model;

import java.util.Collection;

public interface FileAnnotation {
	
	/**
	 * Sets the column position of this warning.
	 * 
	 * @param column the column of this warning
	 */
	public abstract void setColumnPosition(int column);
	
	/**
	 * Sets the column position of this warning.
	 * 
	 * @param columnStart starting column
	 * @param columnEnd ending column
	 */
	public abstract void setColumnPosition(int columnStart, int columnEnd);
	
	/**
	 * Sets the pathname for this warning.
	 * 
	 * @param workspacePath the workspace path
	 */
	public abstract void setPathName(String workspacePath);
	
	public abstract String getPathName();
	
	public abstract String getOrigin();
	
	public abstract int getColumnEnd();
	
	public abstract int getColumnStart();
	
	/**
	 * Sets the origin of this annotation to the specified value.
	 * 
	 * @param origin the value to set
	 */
	public abstract void setOrigin(String origin);
	
	/**
	 * Sets the priority to the specified value.
	 * 
	 * @param priority the value to set
	 */
	public abstract void setPriority(Priority priority);
	
	public abstract String getMessage();
	
	public abstract Priority getPriority();
	
	public abstract long getKey();
	
	public abstract String getFileName();
	
	public abstract String getCategory();
	
	public abstract String getType();
	
	/**
	 * Sets the file name to the specified value.
	 * 
	 * @param fileName the value to set
	 */
	public abstract void setFileName(String fileName);
	
	public abstract String getModuleName();
	
	/**
	 * Sets the module name to the specified value.
	 * 
	 * @param moduleName the value to set
	 */
	public abstract void setModuleName(String moduleName);
	
	public abstract String getPackageName();
	
	/**
	 * Sets the package name to the specified value.
	 * 
	 * @param packageName the value to set
	 */
	public abstract void setPackageName(String packageName);
	
	public abstract Collection<LineRange> getLineRanges();
	
	public abstract int getPrimaryLineNumber();
	
	/**
	 * Returns the line number that should be shown on top of the source code view.
	 * 
	 * @return the line number
	 */
	public abstract int getLinkLineNumber();
	
	/**
	 * Adds another line range to this annotation.
	 * 
	 * @param lineRange the line range to add
	 */
	public abstract void addLineRange(LineRange lineRange);
	
	public abstract long getContextHashCode();
	
	public abstract void setContextHashCode(long contextHashCode);
	
}
