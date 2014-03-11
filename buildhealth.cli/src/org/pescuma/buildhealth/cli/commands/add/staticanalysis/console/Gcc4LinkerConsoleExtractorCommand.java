package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.Gcc4LinkerConsoleExtractor;

@Command(name = "gcc4-linker-console", description = "Add warnings from GNU C Compiler 4 (gcc) output files")
public class Gcc4LinkerConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new Gcc4LinkerConsoleExtractor(getFiles()));
	}

}
