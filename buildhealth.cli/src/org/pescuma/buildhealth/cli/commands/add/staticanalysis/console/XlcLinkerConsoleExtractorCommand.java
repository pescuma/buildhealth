package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.XlcLinkerConsoleExtractor;

@Command(name = "xlc-linker-console", description = "Add warnings from IBM XLC Compiler output files")
public class XlcLinkerConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new XlcLinkerConsoleExtractor(getFiles()));
	}
	
}
