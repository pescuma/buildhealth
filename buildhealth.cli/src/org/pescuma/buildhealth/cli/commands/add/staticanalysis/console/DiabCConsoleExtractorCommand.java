package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.DiabCConsoleExtractor;

@Command(name = "diab-c-console", description = "Add warnings from Wind River Diab Compiler (C/C++) output files")
public class DiabCConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new DiabCConsoleExtractor(getFiles()));
	}
	
}
