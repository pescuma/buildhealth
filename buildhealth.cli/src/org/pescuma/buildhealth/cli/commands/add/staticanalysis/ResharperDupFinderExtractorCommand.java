package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.ResharperDupFinderExtractor;

@Command(name = "resharper-dupfinder", description = "Add code duplication information from ReSharper DupFinder output file")
public class ResharperDupFinderExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new ResharperDupFinderExtractor(getFiles()));
	}
	
}
