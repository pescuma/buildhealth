package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.PhpConsoleExtractor;

@Command(name = "php-console", description = "Add warnings from PHP Runtime output files")
public class PhpConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new PhpConsoleExtractor(getFiles()));
	}
	
}
