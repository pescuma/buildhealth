package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.AcuCobolConsoleExtractor;

@Command(name = "acu-cobol-console", description = "Add warnings from AcuCobol Compiler output files")
public class AcuCobolConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new AcuCobolConsoleExtractor(getFiles()));
	}

}
