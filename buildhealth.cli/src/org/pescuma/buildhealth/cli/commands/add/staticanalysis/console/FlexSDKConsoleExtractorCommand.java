package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.FlexSDKConsoleExtractor;

@Command(name = "flex-console", description = "Add warnings from Flex SDK Compilers (compc & mxmlc) output files")
public class FlexSDKConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new FlexSDKConsoleExtractor(getFiles()));
	}
	
}
