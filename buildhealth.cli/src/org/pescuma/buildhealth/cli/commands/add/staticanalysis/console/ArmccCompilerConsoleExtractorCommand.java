package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.ArmccCompilerConsoleExtractor;

@Command(name = "armcc-console", description = "Add warnings from Armcc Compiler output files")
public class ArmccCompilerConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new ArmccCompilerConsoleExtractor(getFiles()));
	}
	
}
