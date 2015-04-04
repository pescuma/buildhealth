package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.StyleCopExtractor;

@Command(name = "stylecop", description = "Add static code analysis from a StyleCop XML file")
public class StyleCopExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new StyleCopExtractor(getFiles()));
	}
	
}
