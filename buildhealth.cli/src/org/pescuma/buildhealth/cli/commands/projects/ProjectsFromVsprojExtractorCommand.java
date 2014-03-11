package org.pescuma.buildhealth.cli.commands.projects;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.project.ProjectsFromVsprojExtractor;

@Command(name = "from-vsproj", description = "Add projects information from Visual Studio project files (*.vcxproj, *.csproj, *.vbproj, *.fsproj)")
public class ProjectsFromVsprojExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new ProjectsFromVsprojExtractor(getFiles()));
	}
	
}
