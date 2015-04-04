package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.GnuFortranConsoleExtractor;

@Command(name = "gnu-fortran-console", description = "Add warnings from GNU Fortran Compiler (gfortran) output files")
public class GnuFortranConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new GnuFortranConsoleExtractor(getFiles()));
	}
	
}
