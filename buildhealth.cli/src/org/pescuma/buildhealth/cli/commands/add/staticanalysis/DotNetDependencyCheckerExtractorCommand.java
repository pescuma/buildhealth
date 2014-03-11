package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.DotNetDependencyCheckerExtractor;

@Command(name = "dotnet-dependency-checker", description = "Add depedency errors from a dotnet-dependency-checker XML file")
public class DotNetDependencyCheckerExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new DotNetDependencyCheckerExtractor(getFiles()));
	}
	
}
