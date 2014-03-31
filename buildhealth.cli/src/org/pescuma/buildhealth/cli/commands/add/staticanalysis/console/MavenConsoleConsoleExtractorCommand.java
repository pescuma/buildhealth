package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.MavenConsoleConsoleExtractor;

@Command(name = "maven-console", description = "Add warnings from Maven output files")
public class MavenConsoleConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new MavenConsoleConsoleExtractor(getFiles()));
	}

}
