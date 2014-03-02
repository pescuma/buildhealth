package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.DotNetDependencyCheckerExtractor;

@Command(name = "dotnet-dependency-checker", description = "Add depedency errors from a dotnet-dependency-checker XML file")
public class DotNetDependencyCheckerExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with dotnet-dependency-checker XML output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new DotNetDependencyCheckerExtractor(new PseudoFiles(file)));
	}
	
}
