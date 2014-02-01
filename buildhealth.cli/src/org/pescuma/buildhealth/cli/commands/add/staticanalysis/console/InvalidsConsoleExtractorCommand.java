package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.InvalidsConsoleExtractor;

@Command(name = "invalids-console", description = "Add warnings from Oracle Invalids output files")
public class InvalidsConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Oracle Invalids output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new InvalidsConsoleExtractor(new PseudoFiles(file)));
	}

}
