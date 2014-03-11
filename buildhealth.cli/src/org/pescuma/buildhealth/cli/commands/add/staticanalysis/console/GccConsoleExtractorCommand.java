package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.GccConsoleExtractor;

@Command(name = "gcc3-console", description = "Add warnings from GNU C Compiler 3 (gcc) output files")
public class GccConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new GccConsoleExtractor(getFiles()));
	}

}
