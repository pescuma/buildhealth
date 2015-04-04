package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.JavacConsoleExtractor;

@Command(name = "javac-console", description = "Add warnings from Java Compiler (javac) output files")
public class JavacConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new JavacConsoleExtractor(getFiles()));
	}
	
}
