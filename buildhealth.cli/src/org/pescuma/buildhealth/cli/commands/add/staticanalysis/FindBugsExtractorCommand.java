package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.FindBugsExtractor;

@Command(name = "findbugs", description = "Add static code analysis from a FindBugs XML file")
public class FindBugsExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new FindBugsExtractor(getFiles()));
	}
	
}
