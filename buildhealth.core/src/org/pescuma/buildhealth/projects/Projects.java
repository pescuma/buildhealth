package org.pescuma.buildhealth.projects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Projects {
	
	private final Map<String, Project> projects = new HashMap<String, Projects.Project>();
	
	public boolean isEmpty() {
		return projects.isEmpty();
	}
	
	public String findProjectForFile(String filename) {
		if (isEmpty())
			return null;
		
		Set<String> projects = findProjectsForFile(filename);
		
		if (projects.size() == 1)
			return projects.iterator().next();
		else
			return "Unknown project";
	}
	
	private Set<String> findProjectsForFile(String filename) {
		filename = fixPath(filename);
		
		Set<String> result = new HashSet<String>();
		
		for (Project proj : projects.values()) {
			if (proj.isInside(filename))
				result.add(proj.name);
		}
		
		return result;
	}
	
	public void addProjectBasePath(String project, String basePath) {
		basePath = fixPath(basePath);
		if (!basePath.endsWith("/"))
			basePath += "/";
		
		getOrCreateProject(project).basePaths.add(basePath);
	}
	
	public void addProjectFile(String project, String filename) {
		getOrCreateProject(project).files.add(fixPath(filename));
	}
	
	private String fixPath(String path) {
		return path.replace("\\", "/");
	}
	
	private Project getOrCreateProject(String name) {
		if (projects.containsKey(name))
			return projects.get(name);
		
		Project result = new Project(name);
		projects.put(name, result);
		return result;
	}
	
	private static class Project {
		final String name;
		final Set<String> basePaths = new HashSet<String>();
		final Set<String> files = new HashSet<String>();
		
		Project(String name) {
			this.name = name;
		}
		
		boolean isInside(String filename) {
			if (files.contains(filename))
				return true;
			
			for (String base : basePaths)
				if (filename.startsWith(base))
					return true;
			
			return false;
		}
	}
	
}
