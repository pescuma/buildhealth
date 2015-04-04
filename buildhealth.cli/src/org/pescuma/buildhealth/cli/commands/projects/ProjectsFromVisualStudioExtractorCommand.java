package org.pescuma.buildhealth.cli.commands.projects;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.projects.ProjectsFromVisualStudioExtractor;

@Command(name = "from-vs", description = "Add projects information from Visual Studio project files (*.vcproj, *.vcxproj, *.csproj, *.vbproj, *.fsproj)")
public class ProjectsFromVisualStudioExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new ProjectsFromVisualStudioExtractor(getFiles()));
	}
	
}
