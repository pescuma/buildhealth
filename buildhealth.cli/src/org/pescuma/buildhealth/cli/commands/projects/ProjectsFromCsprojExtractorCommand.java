package org.pescuma.buildhealth.cli.commands.projects;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.project.ProjectsFromCsprojExtractor;

@Command(name = "from-csproj", description = "Add projects information from Visual Studio C# project files")
public class ProjectsFromCsprojExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with Visual Studio C# project(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new ProjectsFromCsprojExtractor(new PseudoFiles(file)));
	}
	
}
