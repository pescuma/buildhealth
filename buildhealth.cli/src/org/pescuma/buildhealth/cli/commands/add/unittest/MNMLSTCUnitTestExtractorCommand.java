package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.MNMLSTCUnitTestExtractor;

@Command(name = "mnmlstcUnittest", description = "Add information from a MNMLSTC unittest XML file")
public class MNMLSTCUnitTestExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new MNMLSTCUnitTestExtractor(getFiles()));
	}
	
}
