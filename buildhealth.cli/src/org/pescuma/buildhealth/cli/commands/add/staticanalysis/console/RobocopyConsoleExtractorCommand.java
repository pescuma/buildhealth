package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.RobocopyConsoleExtractor;

@Command(name = "robocopy", description = "Add warnings from Robocopy output files")
public class RobocopyConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Robocopy output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new RobocopyConsoleExtractor(new PseudoFiles(file)));
	}

}
