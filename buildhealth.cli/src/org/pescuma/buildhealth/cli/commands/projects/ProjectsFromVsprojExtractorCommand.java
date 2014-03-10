package org.pescuma.buildhealth.cli.commands.projects;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.project.ProjectsFromVsprojExtractor;

@Command(name = "from-vsproj", description = "Add projects information from Visual Studio project files (vcxproj, csproj, vbproj, fsproj)")
public class ProjectsFromVsprojExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with Visual Studio project(s) (vcxproj, csproj, vbproj, fsproj)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new ProjectsFromVsprojExtractor(new PseudoFiles(file)));
	}
	
}
