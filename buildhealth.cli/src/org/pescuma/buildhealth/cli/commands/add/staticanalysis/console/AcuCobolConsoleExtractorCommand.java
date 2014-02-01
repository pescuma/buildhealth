package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.AcuCobolConsoleExtractor;

@Command(name = "acu-cobol-console", description = "Add warnings from AcuCobol Compiler output files")
public class AcuCobolConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with AcuCobol Compiler output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new AcuCobolConsoleExtractor(new PseudoFiles(file)));
	}

}
