package org.pescuma.buildhealth.projects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pescuma.buildhealth.utils.Location;

public class Projects {
	
	private final Map<String, Project> projects = new HashMap<String, Projects.Project>();
	
	public boolean isEmpty() {
		return projects.isEmpty();
	}
	
	public String findProjectForLocations(List<Location> locations) {
		if (isEmpty())
			return null;
		
		Set<Project> projects = findProjectsForLocations(locations);
		
		if (projects.size() == 1)
			return projects.iterator().next().name;
		else
			return "Unknown project";
		
	}
	
	private Set<Project> findProjectsForLocations(List<Location> locations) {
		Set<Project> result = new HashSet<Project>();
		
		for (Location loc : locations)
			result.addAll(findProjectsForFile(loc.file));
		
		return result;
	}
	
	public String findProjectForFile(String filename) {
		if (isEmpty())
			return null;
		
		Set<Project> projects = findProjectsForFile(filename);
		
		if (projects.size() == 1)
			return projects.iterator().next().name;
		else
			return "Unknown project";
	}
	
	private Set<Project> findProjectsForFile(String filename) {
		filename = fixPath(filename);
		
		Set<Project> result = new HashSet<Project>();
		
		for (Project proj : projects.values()) {
			if (proj.files.contains(filename))
				result.add(proj);
		}
		
		if (!result.isEmpty())
			return result;
		
		// Try by path
		
		for (Project proj : projects.values())
			if (proj.isInside(filename))
				result.add(proj);
		
		if (result.size() < 2)
			return result;
		
		for (Iterator<Project> it = result.iterator(); it.hasNext();) {
			Project proj = it.next();
			
			String projBasePath = proj.findBasePath(filename);
			
			if (hasABetterProject(projBasePath, filename, result)) {
				it.remove();
				continue;
			}
		}
		
		return result;
	}
	
	private boolean hasABetterProject(String projBasePath, String filename, Set<Project> result) {
		for (Project other : result) {
			String otherBasePath = other.findBasePath(filename);
			
			if (otherBasePath.length() > projBasePath.length() && otherBasePath.startsWith(projBasePath))
				return true;
		}
		
		return false;
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
		
		String findBasePath(String filename) {
			for (String base : basePaths)
				if (filename.startsWith(base))
					return base;
			
			return null;
		}
		
		boolean isInside(String filename) {
			return findBasePath(filename) != null;
		}
		
		@Override
		public String toString() {
			return "Project [" + name + "]";
		}
		
	}
	
}
