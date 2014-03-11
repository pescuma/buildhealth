package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.FxCopExtractor;

@Command(name = "fxcop", description = "Add static code analysis from a FxCop XML report file")
public class FxCopExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new FxCopExtractor(getFiles()));
	}
	
}
