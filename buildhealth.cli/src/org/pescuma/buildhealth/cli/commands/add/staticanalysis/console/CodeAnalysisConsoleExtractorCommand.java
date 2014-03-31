package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.CodeAnalysisConsoleExtractor;

@Command(name = "code-analysis-console", description = "Add warnings from CodeAnalysis output files")
public class CodeAnalysisConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new CodeAnalysisConsoleExtractor(getFiles()));
	}

}
