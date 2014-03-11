package org.pescuma.buildhealth.cli.commands.projects;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.project.ProjectsFromVcprojExtractor;

@Command(name = "from-vcproj", description = "Add projects information from old Visual Studio C++ project files (*.vcproj)")
public class ProjectsFromVcprojExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new ProjectsFromVcprojExtractor(getFiles()));
	}
	
}
