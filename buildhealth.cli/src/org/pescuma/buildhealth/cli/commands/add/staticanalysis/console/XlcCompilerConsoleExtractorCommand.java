package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.XlcCompilerConsoleExtractor;

@Command(name = "xlc-console", description = "Add warnings from IBM XLC Compiler output files")
public class XlcCompilerConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new XlcCompilerConsoleExtractor(getFiles()));
	}

}
