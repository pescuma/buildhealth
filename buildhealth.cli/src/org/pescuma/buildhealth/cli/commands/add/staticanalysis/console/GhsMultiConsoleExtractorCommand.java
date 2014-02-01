package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.GhsMultiConsoleExtractor;

@Command(name = "chs-console", description = "Add warnings from GHS Multi Compiler output files")
public class GhsMultiConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with GHS Multi Compiler output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new GhsMultiConsoleExtractor(new PseudoFiles(file)));
	}

}
