package org.pescuma.buildhealth.cli.commands.projects;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.projects.ProjectsFromEclipseExtractor;

@Command(name = "from-eclipse", description = "Add projects information from Eclipse project files (.project)")
public class ProjectsFromEclipseExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new ProjectsFromEclipseExtractor(getFiles()));
	}
	
}
