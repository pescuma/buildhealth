package org.pescuma.buildhealth.cli.commands.projects;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.project.ProjectsFromEclipseExtractor;

@Command(name = "from-eclipse", description = "Add projects information from eclipse project files")
public class ProjectsFromEclipseExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with eclipse project(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new ProjectsFromEclipseExtractor(new PseudoFiles(file)));
	}
	
}
