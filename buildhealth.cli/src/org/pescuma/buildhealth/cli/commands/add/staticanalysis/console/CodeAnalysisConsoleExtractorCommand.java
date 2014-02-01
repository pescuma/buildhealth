package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.CodeAnalysisConsoleExtractor;

@Command(name = "code-analysis-console", description = "Add warnings from CodeAnalysis output files")
public class CodeAnalysisConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with CodeAnalysis output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new CodeAnalysisConsoleExtractor(new PseudoFiles(file)));
	}

}
