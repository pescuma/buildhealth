package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.CppTestExtractor;

@Command(name = "cpptest", description = "Add information from a CppTest XML file")
public class CppTestExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new CppTestExtractor(getFiles()));
	}
	
}
