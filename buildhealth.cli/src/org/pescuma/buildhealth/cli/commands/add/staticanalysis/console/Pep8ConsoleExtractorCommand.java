package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.Pep8ConsoleExtractor;

@Command(name = "pep8-console", description = "Add warnings from Pep8 output files")
public class Pep8ConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new Pep8ConsoleExtractor(getFiles()));
	}

}
