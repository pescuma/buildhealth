package org.pescuma.buildhealth.cli.commands.add.japex;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.performance.JapexExtractor;

@Command(name = "japex", description = "Add performance information from an Japex report XML file")
public class JapexExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new JapexExtractor(getFiles()));
	}
	
}
