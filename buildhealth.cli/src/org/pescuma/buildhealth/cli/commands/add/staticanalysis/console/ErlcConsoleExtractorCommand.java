package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.ErlcConsoleExtractor;

@Command(name = "erlc-console", description = "Add warnings from Erlang Compiler (erlc) output files")
public class ErlcConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new ErlcConsoleExtractor(getFiles()));
	}

}
