package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.IarConsoleExtractor;

@Command(name = "iar-console", description = "Add warnings from IAR Compiler (C/C++) output files")
public class IarConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new IarConsoleExtractor(getFiles()));
	}
	
}
