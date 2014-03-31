package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.JavaDocConsoleExtractor;

@Command(name = "javadoc-console", description = "Add warnings from JavaDoc Tool output files")
public class JavaDocConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new JavaDocConsoleExtractor(getFiles()));
	}

}
