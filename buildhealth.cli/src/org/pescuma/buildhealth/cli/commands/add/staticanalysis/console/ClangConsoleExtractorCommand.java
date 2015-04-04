package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.ClangConsoleExtractor;

@Command(name = "clang-console", description = "Add warnings from Clang (LLVM based) output files")
public class ClangConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new ClangConsoleExtractor(getFiles()));
	}
	
}
