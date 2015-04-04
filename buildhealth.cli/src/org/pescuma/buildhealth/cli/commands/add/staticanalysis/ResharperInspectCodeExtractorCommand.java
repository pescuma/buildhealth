package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.ResharperInspectCodeExtractor;

@Command(name = "resharper-inspectcode", description = "Add code duplication information from ReSharper InspectCode output file")
public class ResharperInspectCodeExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new ResharperInspectCodeExtractor(getFiles()));
	}
	
}
