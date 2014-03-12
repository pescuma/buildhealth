package org.pescuma.buildhealth.analyser.tasks;

import static org.pescuma.buildhealth.analyser.NumbersFormater.*;

import java.util.List;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.utils.Location;

public class TaskReport extends Report {
	
	private final String id;
	private final String owner;
	private final String createdBy;
	private final String creationDate;
	private final String taskType;
	private final String taskStatus;
	private final String details;
	private final List<Location> locations;
	private final double count;
	
	public TaskReport(BuildStatus status, String id, String text, String owner, String createdBy, String creationDate,
			String taskType, String taskStatus, String details, List<Location> locations, double count,
			boolean isSourceOfProblem, List<Report> children) {
		super(status, text, "", createDescription(id, taskType, taskStatus, owner, count), isSourceOfProblem, children);
		this.id = id;
		this.owner = owner;
		this.createdBy = createdBy;
		this.creationDate = creationDate;
		this.taskType = taskType;
		this.taskStatus = taskStatus;
		this.details = details;
		this.locations = locations;
		this.count = count;
	}
	
	private static String createDescription(String id, String type, String taskStatus, String owner, double count) {
		StringBuilder result = new StringBuilder();
		append(result, type);
		append(result, taskStatus);
		append(result, "id", id);
		append(result, "owner", owner);
		
		String countStr = format1000(count);
		if (!countStr.equals("1"))
			append(result, "representing " + countStr + " tasks");
		
		return result.toString();
	}
	
	private static void append(StringBuilder result, String name, String value) {
		if (value.isEmpty())
			return;
		
		if (result.length() > 0)
			result.append(", ");
		
		result.append(name).append(": ").append(value);
	}
	
	private static void append(StringBuilder result, String text) {
		if (text.isEmpty())
			return;
		
		if (result.length() > 0)
			result.append(", ");
		
		result.append(text);
	}
	
	public String getId() {
		return id;
	}
	
	public String getText() {
		return getName();
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public String getTaskType() {
		return taskType;
	}
	
	public String getTaskStatus() {
		return taskStatus;
	}
	
	public String getDetails() {
		return details;
	}
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public double getCount() {
		return count;
	}
	
}
