package org.pescuma.buildhealth.projects;

import org.pescuma.buildhealth.core.BuildData;
import org.pescuma.buildhealth.core.BuildData.Line;
import org.pescuma.buildhealth.prefs.Preferences;

/**
 * Expect the lines to be:
 * 
 * <pre>
 * Project,name,BasePath,folder
 * Project,name,File,filename
 * </pre>
 * 
 * Example:
 * 
 * <pre>
 * 0 | Project,My Project,BasePath,/src/myproject
 * 0 | Project,My Project,File,/src/myproject/myfile.java
 * </pre>
 */
public class ProjectsAnalyser {
	
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_TYPE = 2;
	public static final int COLUMN_FOLDER_OR_FILE = 3;
	
	public Projects computeProjects(BuildData data, Preferences prefs) {
		Projects result = new Projects();
		
		data = data.filter("Project");
		if (data.isEmpty())
			return result;
		
		for (Line line : data.filter(COLUMN_TYPE, "BasePath").getLines())
			result.addProjectBasePath(line.getColumn(COLUMN_NAME), line.getColumn(COLUMN_FOLDER_OR_FILE));
		
		for (Line line : data.filter(COLUMN_TYPE, "File").getLines())
			result.addProjectFile(line.getColumn(COLUMN_NAME), line.getColumn(COLUMN_FOLDER_OR_FILE));
		
		return result;
	}
	
}
