package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.FPCUnitExtractor;

@Command(name = "fpcunit", description = "Add information from a FPCUnit XML file")
public class FPCUnitExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new FPCUnitExtractor(getFiles()));
	}
	
}
