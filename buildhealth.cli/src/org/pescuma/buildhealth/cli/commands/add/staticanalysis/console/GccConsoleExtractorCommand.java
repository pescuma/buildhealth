package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.GccConsoleExtractor;

@Command(name = "gcc3", description = "Add warnings from GNU Compiler 3 (gcc) output files")
public class GccConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with GNU Compiler 3 (gcc) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new GccConsoleExtractor(new PseudoFiles(file)));
	}

}
