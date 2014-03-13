package org.pescuma.buildhealth.core;

import static java.util.Arrays.*;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class Report {
	
	private final BuildStatus status;
	private final String name;
	private final String value;
	private final String description;
	private final String problemDescription;
	private final List<Report> children;
	
	public Report(BuildStatus status, String name, String value, Report... children) {
		this(status, name, value, null, null, asList(children));
	}
	
	public Report(BuildStatus status, String name, String value, String description, Report... children) {
		this(status, name, value, description, null, asList(children));
	}
	
	public Report(BuildStatus status, String name, String value, String description, String problemDescription,
			Report... children) {
		this(status, name, value, description, problemDescription, asList(children));
	}
	
	public Report(BuildStatus status, String name, String value, String description, String problemDescription,
			List<Report> children) {
		if (status == null)
			throw new IllegalArgumentException();
		if (Strings.isNullOrEmpty(name))
			throw new IllegalArgumentException();
		
		this.status = status;
		this.name = name;
		this.value = value;
		this.description = Objects.firstNonNull(description, "");
		this.problemDescription = problemDescription;
		
		List<Report> list = new ArrayList<Report>();
		if (children != null)
			list.addAll(children);
		this.children = list;
	}
	
	public BuildStatus getStatus() {
		return status;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isSourceOfProblem() {
		return problemDescription != null;
	}
	
	public String getProblemDescription() {
		return problemDescription;
	}
	
	public List<Report> getChildren() {
		return children;
	}
	
	public void visit(Visitor visitor) {
		visitor.preVisit(this);
		
		for (Report child : children)
			child.visit(visitor);
		
		visitor.posVisit(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((problemDescription == null) ? 0 : problemDescription.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Report other = (Report) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (problemDescription == null) {
			if (other.problemDescription != null)
				return false;
		} else if (!problemDescription.equals(other.problemDescription))
			return false;
		if (status != other.status)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Report [" + status + ", " + name + "=" + value + ", desc=" + description
				+ (problemDescription != null ? ", source of problem: " + problemDescription : "")
				+ (children.isEmpty() ? "" : ", " + children) + "]";
	}
	
	public static BuildStatus mergeBuildStatus(List<Report> reports) {
		BuildStatus status = BuildStatus.Good;
		for (Report report : reports)
			status = BuildStatus.merge(status, report.getStatus());
		return status;
	}
	
	public static class Visitor {
		public void preVisit(Report report) {
		}
		
		public void posVisit(Report report) {
		}
	}
	
}
