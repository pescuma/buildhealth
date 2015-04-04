package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.TusarExtractor;

@Command(name = "tusar", description = "Add information from a Tusar XML file")
public class TusarExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new TusarExtractor(getFiles()));
	}
	
}
