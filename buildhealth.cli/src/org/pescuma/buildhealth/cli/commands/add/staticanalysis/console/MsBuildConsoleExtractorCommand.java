package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.MsBuildConsoleExtractor;

@Command(name = "msbuild-console", description = "Add warnings from MSBuild output files")
public class MsBuildConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new MsBuildConsoleExtractor(getFiles()));
	}

}
