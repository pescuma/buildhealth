package hudson.plugins.analysis.util.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Based on work by Ulli Hafner
 */
public abstract class AbstractAnnotation implements FileAnnotation {
	private static final String DEFAULT_PACKAGE = "Default Package";
	/** UNIX path separator. */
	private static final String SLASH = "/";
	/** Current key of this annotation. */
	private static long currentKey;
	
	/** The message of this annotation. */
	private final/* almost final */String message;
	/** The priority of this annotation. */
	private Priority priority;
	/** Unique key of this annotation. */
	private final long key;
	/**
	 * The ordered list of line ranges that show the origin of the annotation in the associated file.
	 */
	private final ArrayList<LineRange> lineRanges;
	/** Primary line number of this warning, i.e., the start line of the first line range. */
	private final int primaryLineNumber;
	/** The filename of the class that contains this annotation. */
	private String fileName;
	/** The name of the maven or ant module that contains this annotation. */
	private String moduleName;
	/** The name of the package (or name space) that contains this annotation. */
	private String packageName;
	/** Bug category. */
	private final/* almost final */String category;
	/** Bug type. */
	private final/* almost final */String type;
	/**
	 * Context hash code of this annotation. This hash code is used to decide if two annotations are equal even if the
	 * equals method returns <code>false</code>.
	 */
	private long contextHashCode;
	/** The origin of this warning. */
	private String origin;
	/** Relative path of this duplication. @since 1.10 */
	private String pathName;
	/** Column start of primary line range of warning. @since 1.38 */
	private int primaryColumnStart;
	/** Column end of primary line range of warning. @since 1.38 */
	private int primaryColumnEnd;
	
	/**
	 * Creates a new instance of <code>AbstractAnnotation</code>.
	 * 
	 * @param message the message of the warning
	 * @param start the first line of the line range
	 * @param end the last line of the line range
	 * @param category the category of the annotation
	 * @param type the type of the annotation
	 */
	public AbstractAnnotation(final String message, final int start, final int end, final String category,
			final String type) {
		this.message = StringUtils.strip(StringEscapeUtils.escapeXml(message));
		this.category = StringUtils.defaultString(category);
		this.type = StringUtils.defaultString(type);
		
		key = currentKey++;
		
		lineRanges = new ArrayList<LineRange>();
		lineRanges.add(new LineRange(start, end));
		primaryLineNumber = start;
		
		contextHashCode = currentKey;
	}
	
	/**
	 * Creates a new instance of <code>AbstractAnnotation</code>.
	 * 
	 * @param priority the priority
	 * @param message the message of the warning
	 * @param start the first line of the line range
	 * @param end the last line of the line range
	 * @param category the category of the annotation
	 * @param type the type of the annotation
	 */
	public AbstractAnnotation(final Priority priority, final String message, final int start, final int end,
			final String category, final String type) {
		this(message, start, end, category, type);
		this.priority = priority;
	}
	
	/**
	 * Copy constructor: Creates a new instance of {@link AbstractAnnotation}.
	 * 
	 * @param copy the annotation to copy the values from
	 */
	public AbstractAnnotation(final FileAnnotation copy) {
		key = currentKey++;
		
		message = copy.getMessage();
		priority = copy.getPriority();
		primaryLineNumber = copy.getPrimaryLineNumber();
		lineRanges = new ArrayList<LineRange>(copy.getLineRanges());
		
		contextHashCode = copy.getContextHashCode();
		
		fileName = copy.getFileName();
		category = copy.getCategory();
		type = copy.getType();
		moduleName = copy.getModuleName();
		packageName = copy.getPackageName();
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#setColumnPosition(int) */
	@Override
	public void setColumnPosition(final int column) {
		setColumnPosition(column, column);
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#setColumnPosition(int, int) */
	@Override
	public void setColumnPosition(final int columnStart, final int columnEnd) {
		primaryColumnStart = columnStart;
		primaryColumnEnd = columnEnd;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#setPathName(java.lang.String) */
	@Override
	public void setPathName(final String workspacePath) {
		String normalized = workspacePath.replace('\\', '/');
		
		String s = StringUtils.removeStart(getFileName(), normalized);
		s = StringUtils.remove(s, FilenameUtils.getName(getFileName()));
		s = StringUtils.removeStart(s, SLASH);
		s = StringUtils.removeEnd(s, SLASH);
		pathName = s;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getPathName() */
	@Override
	public String getPathName() {
		return pathName;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getOrigin() */
	@Override
	public String getOrigin() {
		return StringUtils.defaultString(origin);
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getColumnEnd() */
	@Override
	public int getColumnEnd() {
		return primaryColumnEnd;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getColumnStart() */
	@Override
	public int getColumnStart() {
		return primaryColumnStart;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#setOrigin(java.lang.String) */
	@Override
	public void setOrigin(final String origin) {
		this.origin = origin;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#setPriority(hudson.plugins.analysis.util.model.Priority) */
	@Override
	public void setPriority(final Priority priority) {
		this.priority = priority;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getMessage() */
	@Override
	public String getMessage() {
		return message;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getPriority() */
	@Override
	public Priority getPriority() {
		return priority;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getKey() */
	@Override
	public final long getKey() {
		return key;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getFileName() */
	@Override
	public final String getFileName() {
		return fileName;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getCategory() */
	@Override
	public String getCategory() {
		return category;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getType() */
	@Override
	public String getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#setFileName(java.lang.String) */
	@Override
	public final void setFileName(final String fileName) {
		this.fileName = StringUtils.strip(fileName).replace('\\', '/');
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getModuleName() */
	@Override
	public final String getModuleName() {
		return StringUtils.defaultIfEmpty(moduleName, "Default Module");
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#setModuleName(java.lang.String) */
	@Override
	public final void setModuleName(final String moduleName) {
		this.moduleName = moduleName;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getPackageName() */
	@Override
	public final String getPackageName() {
		return StringUtils.defaultIfEmpty(packageName, DEFAULT_PACKAGE);
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#setPackageName(java.lang.String) */
	@Override
	public final void setPackageName(final String packageName) {
		this.packageName = packageName;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getLineRanges() */
	@Override
	public final Collection<LineRange> getLineRanges() {
		return Collections.unmodifiableCollection(lineRanges);
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getPrimaryLineNumber() */
	@Override
	public final int getPrimaryLineNumber() {
		return primaryLineNumber;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getLinkLineNumber() */
	@Override
	public final int getLinkLineNumber() {
		return Math.max(1, primaryLineNumber - 10);
	}
	
	/* (non-Javadoc)
	 * 
	 * @see
	 * hudson.plugins.analysis.util.model.FileAnnotation#addLineRange(hudson.plugins.analysis.util.model.LineRange) */
	@Override
	public void addLineRange(final LineRange lineRange) {
		if (!lineRanges.contains(lineRange)) {
			lineRanges.add(lineRange);
		}
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#getContextHashCode() */
	@Override
	public long getContextHashCode() {
		return contextHashCode;
	}
	
	/* (non-Javadoc)
	 * 
	 * @see hudson.plugins.analysis.util.model.FileAnnotation#setContextHashCode(long) */
	@Override
	public void setContextHashCode(final long contextHashCode) {
		this.contextHashCode = contextHashCode;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((lineRanges == null) ? 0 : lineRanges.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + primaryLineNumber;
		result = prime * result + primaryColumnStart;
		result = prime * result + primaryColumnEnd;
		result = prime * result + ((priority == null) ? 0 : priority.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractAnnotation other = (AbstractAnnotation) obj;
		if (category == null) {
			if (other.category != null) {
				return false;
			}
		} else if (!category.equals(other.category)) {
			return false;
		}
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		} else if (!fileName.toString().equals(other.fileName.toString())) {
			return false;
		}
		if (lineRanges == null) {
			if (other.lineRanges != null) {
				return false;
			}
		} else if (!lineRanges.equals(other.lineRanges)) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.toString().equals(other.message.toString())) {
			return false;
		}
		if (!getModuleName().equals(other.getModuleName())) {
			return false;
		}
		if (!getPackageName().equals(other.getPackageName())) {
			return false;
		}
		if (primaryLineNumber != other.primaryLineNumber) {
			return false;
		}
		if (primaryColumnStart != other.primaryColumnStart) {
			return false;
		}
		if (primaryColumnEnd != other.primaryColumnEnd) {
			return false;
		}
		if (priority == null) {
			if (other.priority != null) {
				return false;
			}
		} else if (!priority.equals(other.priority)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}
	
	public boolean hasPackageName() {
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s):%s,%s,%s:%s", getFileName(), primaryLineNumber, priority, getCategory(),
				getType(), getMessage());
	}
}
