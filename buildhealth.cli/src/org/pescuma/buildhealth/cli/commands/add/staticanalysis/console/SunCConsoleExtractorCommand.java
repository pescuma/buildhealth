package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.SunCConsoleExtractor;

@Command(name = "sun-c-console", description = "Add warnings from SUN C++ Compiler output files")
public class SunCConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new SunCConsoleExtractor(getFiles()));
	}
	
}
