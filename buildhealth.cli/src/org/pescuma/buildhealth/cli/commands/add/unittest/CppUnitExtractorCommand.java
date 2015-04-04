package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.CppUnitExtractor;

@Command(name = "cppunit", description = "Add information from a CppUnit XML file")
public class CppUnitExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new CppUnitExtractor(getFiles()));
	}
	
}
