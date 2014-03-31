package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.P4ConsoleExtractor;

@Command(name = "p4-console", description = "Add warnings from Perforce Compiler output files")
public class P4ConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new P4ConsoleExtractor(getFiles()));
	}

}
