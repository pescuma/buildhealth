package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.IntelConsoleExtractor;

@Command(name = "intel-console", description = "Add warnings from Intel C Compiler output files")
public class IntelConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new IntelConsoleExtractor(getFiles()));
	}

}
