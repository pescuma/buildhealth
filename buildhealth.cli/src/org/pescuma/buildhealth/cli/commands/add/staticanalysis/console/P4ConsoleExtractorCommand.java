package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.P4ConsoleExtractor;

@Command(name = "p4", description = "Add warnings from Perforce Compiler output files")
public class P4ConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Perforce Compiler output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new P4ConsoleExtractor(new PseudoFiles(file)));
	}

}
