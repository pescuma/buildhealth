package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.DiabCConsoleExtractor;

@Command(name = "diab-c", description = "Add warnings from Wind River Diab Compiler (C/C++) output files")
public class DiabCConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Wind River Diab Compiler (C/C++) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new DiabCConsoleExtractor(new PseudoFiles(file)));
	}

}
