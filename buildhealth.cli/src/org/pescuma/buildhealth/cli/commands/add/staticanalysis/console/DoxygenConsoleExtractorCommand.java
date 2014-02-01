package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.DoxygenConsoleExtractor;

@Command(name = "doxygen-console", description = "Add warnings from Doxygen output files")
public class DoxygenConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Doxygen output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new DoxygenConsoleExtractor(new PseudoFiles(file)));
	}

}
