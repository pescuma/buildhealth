package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.PhpConsoleExtractor;

@Command(name = "php-console", description = "Add warnings from PHP Runtime output files")
public class PhpConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with PHP Runtime output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new PhpConsoleExtractor(new PseudoFiles(file)));
	}

}
