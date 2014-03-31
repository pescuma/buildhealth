package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.TnsdlConsoleExtractor;

@Command(name = "tnsdl-console", description = "Add warnings from TNSDL Translator output files")
public class TnsdlConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new TnsdlConsoleExtractor(getFiles()));
	}

}
