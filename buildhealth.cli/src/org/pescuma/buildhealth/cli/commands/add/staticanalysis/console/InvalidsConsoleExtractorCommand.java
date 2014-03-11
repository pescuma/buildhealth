package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.InvalidsConsoleExtractor;

@Command(name = "invalids-console", description = "Add warnings from Oracle Invalids output files")
public class InvalidsConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new InvalidsConsoleExtractor(getFiles()));
	}

}
