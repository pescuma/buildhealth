package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.BoostTestExtractor;

@Command(name = "boosttest", description = "Add information from a Boost Test XML file")
public class BoostTestExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new BoostTestExtractor(getFiles()));
	}
	
}
