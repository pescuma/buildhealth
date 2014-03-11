package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.AUnitExtractor;

@Command(name = "aunit", description = "Add information from a AUnit XML file")
public class AUnitExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new AUnitExtractor(getFiles()));
	}
	
}
