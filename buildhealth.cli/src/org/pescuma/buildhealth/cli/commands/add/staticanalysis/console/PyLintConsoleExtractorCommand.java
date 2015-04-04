package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.PyLintConsoleExtractor;

@Command(name = "py-lint-console", description = "Add warnings from PyLint output files")
public class PyLintConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new PyLintConsoleExtractor(getFiles()));
	}
	
}
