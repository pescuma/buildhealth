package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.EclipseConsoleExtractor;

@Command(name = "eclipse-console", description = "Add warnings from Java Compiler (Eclipse) output files")
public class EclipseConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new EclipseConsoleExtractor(getFiles()));
	}

}
