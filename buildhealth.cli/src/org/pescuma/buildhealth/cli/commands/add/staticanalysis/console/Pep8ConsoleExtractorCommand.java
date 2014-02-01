package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.Pep8ConsoleExtractor;

@Command(name = "pep8-console", description = "Add warnings from Pep8 output files")
public class Pep8ConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Pep8 output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new Pep8ConsoleExtractor(new PseudoFiles(file)));
	}

}
