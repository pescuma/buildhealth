package org.pescuma.buildhealth.cli.commands.add;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.loc.CLOCExtractor;

@Command(name = "cloc", description = "Add lines of code information from cloc csv output")
public class CLOCExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new CLOCExtractor(getFiles()));
	}
	
}
