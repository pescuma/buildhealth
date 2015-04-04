package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.PMDExtractor;

@Command(name = "pmd", description = "Add static code analysis from a PMD XML file")
public class PMDExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new PMDExtractor(getFiles()));
	}
	
}
