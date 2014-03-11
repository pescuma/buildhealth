package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.NagFortranConsoleExtractor;

@Command(name = "nag-fortran-console", description = "Add warnings from NAG Fortran Compiler (nagfor) output files")
public class NagFortranConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new NagFortranConsoleExtractor(getFiles()));
	}

}
