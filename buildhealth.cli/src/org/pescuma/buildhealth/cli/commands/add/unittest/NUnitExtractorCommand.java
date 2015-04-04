package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.NUnitExtractor;

@Command(name = "nunit", description = "Add information from a NUnit XML file")
public class NUnitExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new NUnitExtractor(getFiles()));
	}
	
}
