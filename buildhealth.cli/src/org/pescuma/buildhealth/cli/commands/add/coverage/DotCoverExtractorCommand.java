package org.pescuma.buildhealth.cli.commands.add.coverage;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.coverage.DotCoverExtractor;

@Command(name = "dotCover", description = "Add coverage information from an dotCover XML file")
public class DotCoverExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new DotCoverExtractor(getFiles()));
	}
	
}
