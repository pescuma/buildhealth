package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.BuckminsterConsoleExtractor;

@Command(name = "buckminster-console", description = "Add warnings from Buckminster output files")
public class BuckminsterConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Buckminster output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new BuckminsterConsoleExtractor(new PseudoFiles(file)));
	}

}
