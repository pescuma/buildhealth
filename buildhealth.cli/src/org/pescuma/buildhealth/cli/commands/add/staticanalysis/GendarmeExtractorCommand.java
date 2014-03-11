package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.GendarmeExtractor;

@Command(name = "gendarme", description = "Add static code analysis from a Gendarme XML file")
public class GendarmeExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new GendarmeExtractor(getFiles()));
	}
	
}
