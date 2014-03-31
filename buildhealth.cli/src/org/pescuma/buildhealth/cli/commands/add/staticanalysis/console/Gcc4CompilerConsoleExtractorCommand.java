package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.Gcc4CompilerConsoleExtractor;

@Command(name = "gcc4-console", description = "Add warnings from GNU C Compiler 4 (gcc) output files")
public class Gcc4CompilerConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new Gcc4CompilerConsoleExtractor(getFiles()));
	}

}
