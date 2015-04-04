package org.pescuma.buildhealth.cli.commands.add.coverage;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.coverage.JacocoExtractor;

@Command(name = "jacoco", description = "Add coverage information from an JaCoCo XML file")
public class JacocoExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new JacocoExtractor(getFiles()));
	}
	
}
