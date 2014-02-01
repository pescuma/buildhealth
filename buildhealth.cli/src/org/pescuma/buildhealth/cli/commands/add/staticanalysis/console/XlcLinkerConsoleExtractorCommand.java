package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.XlcLinkerConsoleExtractor;

@Command(name = "xlc-linker-console", description = "Add warnings from IBM XLC Compiler output files")
public class XlcLinkerConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with IBM XLC Compiler output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new XlcLinkerConsoleExtractor(new PseudoFiles(file)));
	}

}
