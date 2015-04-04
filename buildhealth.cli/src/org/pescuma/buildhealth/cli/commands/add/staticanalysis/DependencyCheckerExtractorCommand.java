package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.DependencyCheckerExtractor;

@Command(name = "dependency-checker", description = "Add depedency errors from a Dependency Checker XML file")
public class DependencyCheckerExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new DependencyCheckerExtractor(getFiles()));
	}
	
}
