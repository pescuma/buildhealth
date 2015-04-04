package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.unittest.xunit.PHPUnitExtractor;

@Command(name = "phpunit", description = "Add information from a PHPUnit XML file")
public class PHPUnitExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new PHPUnitExtractor(getFiles()));
	}
	
}
