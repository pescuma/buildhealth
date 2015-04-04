package org.pescuma.buildhealth.cli.commands.add.coverage;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.coverage.EmmaExtractor;

@Command(name = "emma", description = "Add coverage information from an Emma XML file")
public class EmmaExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new EmmaExtractor(getFiles()));
	}
	
}
