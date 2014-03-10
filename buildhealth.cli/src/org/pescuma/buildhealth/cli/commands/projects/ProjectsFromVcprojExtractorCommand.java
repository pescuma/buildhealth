package org.pescuma.buildhealth.cli.commands.projects;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.project.ProjectsFromVcprojExtractor;

@Command(name = "from-vcproj", description = "Add projects information from old Visual Studio C++ project files (*.vcproj)")
public class ProjectsFromVcprojExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with Visual Studio C++ project(s) (*.vcproj)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new ProjectsFromVcprojExtractor(new PseudoFiles(file)));
	}
	
}
