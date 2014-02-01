package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.GnuFortranConsoleExtractor;

@Command(name = "gnu-fortran-console", description = "Add warnings from GNU Fortran Compiler (gfortran) output files")
public class GnuFortranConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with GNU Fortran Compiler (gfortran) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new GnuFortranConsoleExtractor(new PseudoFiles(file)));
	}

}
