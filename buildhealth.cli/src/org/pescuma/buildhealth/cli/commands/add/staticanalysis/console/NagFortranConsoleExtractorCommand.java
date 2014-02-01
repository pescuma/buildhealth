package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.NagFortranConsoleExtractor;

@Command(name = "nag-fortran-console", description = "Add warnings from NAG Fortran Compiler (nagfor) output files")
public class NagFortranConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with NAG Fortran Compiler (nagfor) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new NagFortranConsoleExtractor(new PseudoFiles(file)));
	}

}
