package org.pescuma.buildhealth.analyser.tasks;

import static org.pescuma.buildhealth.analyser.NumbersFormater.*;

import java.util.List;

import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;

public class TaskReport extends Report {
	
	private final String id;
	private final String owner;
	private final String createdBy;
	private final String creationDate;
	private final String type;
	private final String taskStatus;
	private final String details;
	private final String file;
	private final String fileLine;
	private final double count;
	
	public TaskReport(BuildStatus status, String id, String text, String owner, String createdBy, String creationDate,
			String type, String taskStatus, String details, String file, String fileLine, double count,
			List<Report> children) {
		super(status, text, "", createDescription(id, type, taskStatus, owner, count), children);
		this.id = id;
		this.owner = owner;
		this.createdBy = createdBy;
		this.creationDate = creationDate;
		this.type = type;
		this.taskStatus = taskStatus;
		this.details = details;
		this.file = file;
		this.fileLine = fileLine;
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
	
	public String getType() {
		return type;
	}
	
	public String getTaskStatus() {
		return taskStatus;
	}
	
	public String getDetails() {
		return details;
	}
	
	public String getFile() {
		return file;
	}
	
	public String getFileLine() {
		return fileLine;
	}
	
	public double getCount() {
		return count;
	}
	
}
