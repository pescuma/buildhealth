package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.MSTestExtractor;

@Command(name = "mstest", description = "Add information from a MSTest XML file")
public class MSTestExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new MSTestExtractor(getFiles()));
	}
	
}
