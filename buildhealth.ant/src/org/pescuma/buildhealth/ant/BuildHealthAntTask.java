package org.pescuma.buildhealth.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.pescuma.buildhealth.core.BuildHealth;
import org.pescuma.buildhealth.core.BuildHealth.ReportFlags;
import org.pescuma.buildhealth.core.BuildStatus;
import org.pescuma.buildhealth.core.Report;
import org.pescuma.buildhealth.notifiers.BuildHealthNotifierTracker;
import org.pescuma.buildhealth.utils.ReportFormater;

public class BuildHealthAntTask extends Task implements TaskContainer {
	
	private final List<Task> tasks = new ArrayList<Task>();
	private File home;
	private boolean failOnError = true;
	private boolean report = true;
	private boolean notify = false;
	
	public File getHome() {
		return home;
	}
	
	public void setHome(File home) {
		this.home = home;
	}
	
	public boolean isFailOnError() {
		return failOnError;
	}
	
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}
	
	public boolean isReport() {
		return report;
	}
	
	public void setReport(boolean report) {
		this.report = report;
	}
	
	public boolean isNotify() {
		return notify;
	}
	
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	
	@Override
	public void addTask(Task task) {
		tasks.add(task);
	}
	
	public static BuildHealth buildHealth;
	
	@Override
	public void execute() throws BuildException {
		buildHealth = new BuildHealth(BuildHealth.findHome(home, true));
		
		buildHealth.startNewBuild();
		
		for (Task task : tasks)
			task.perform();
		
		Report buildReport = buildHealth.generateReport(ReportFlags.SummaryOnly | ReportFlags.ListSourcesOfProblems);
		
		if (report)
			log(new ReportFormater().writeBuildStatuses().format(buildReport).trim());
		
		if (notify)
			buildHealth.sendNotifications(new BuildHealthNotifierTracker() {
				@Override
				public void reportNotified(String message) {
					log(message);
				}
			});
		
		buildHealth.shutdown();
		buildHealth = null;
		
		if (failOnError && (buildReport == null || buildReport.getStatus() != BuildStatus.Good))
			throw new BuildException(new ReportFormater().createSummaryLine(buildReport), getLocation());
	}
	
}
