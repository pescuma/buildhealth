package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.JUnitExtractor;

@Command(name = "junit", description = "Add information from a JUnit XML file")
public class JUnitExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new JUnitExtractor(getFiles()));
	}
	
}
