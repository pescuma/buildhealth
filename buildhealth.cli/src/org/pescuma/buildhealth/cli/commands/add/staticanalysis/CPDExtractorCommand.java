package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.CPDExtractor;

@Command(name = "cpd", description = "Add code duplication information from a CPD TXT file")
public class CPDExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new CPDExtractor(getFiles()));
	}
	
}
