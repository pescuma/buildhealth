package org.pescuma.buildhealth.cli.commands.add.coverage;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.coverage.OpenCoverExtractor;

@Command(name = "opencover", description = "Add coverage information from an OpenCover XML file")
public class OpenCoverExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new OpenCoverExtractor(getFiles()));
	}
	
}
