package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.PyLintConsoleExtractor;

@Command(name = "py-lint-console", description = "Add warnings from PyLint output files")
public class PyLintConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with PyLint output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new PyLintConsoleExtractor(new PseudoFiles(file)));
	}

}
