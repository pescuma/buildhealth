package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.BuckminsterConsoleExtractor;

@Command(name = "buckminster-console", description = "Add warnings from Buckminster output files")
public class BuckminsterConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new BuckminsterConsoleExtractor(getFiles()));
	}
	
}
