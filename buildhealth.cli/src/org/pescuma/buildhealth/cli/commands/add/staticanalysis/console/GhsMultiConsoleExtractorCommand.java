package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.GhsMultiConsoleExtractor;

@Command(name = "chs-console", description = "Add warnings from GHS Multi Compiler output files")
public class GhsMultiConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new GhsMultiConsoleExtractor(getFiles()));
	}
	
}
