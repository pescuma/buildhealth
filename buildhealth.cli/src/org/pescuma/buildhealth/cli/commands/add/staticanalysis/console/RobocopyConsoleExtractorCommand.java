package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.RobocopyConsoleExtractor;

@Command(name = "robocopy-console", description = "Add warnings from Robocopy output files")
public class RobocopyConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new RobocopyConsoleExtractor(getFiles()));
	}
	
}
