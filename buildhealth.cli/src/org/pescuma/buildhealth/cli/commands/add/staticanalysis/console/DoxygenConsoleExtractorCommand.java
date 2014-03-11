package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.DoxygenConsoleExtractor;

@Command(name = "doxygen-console", description = "Add warnings from Doxygen output files")
public class DoxygenConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new DoxygenConsoleExtractor(getFiles()));
	}

}
