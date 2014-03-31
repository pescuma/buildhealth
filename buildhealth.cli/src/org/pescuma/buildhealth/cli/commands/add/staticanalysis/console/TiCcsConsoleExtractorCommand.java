package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.TiCcsConsoleExtractor;

@Command(name = "ticcs-console", description = "Add warnings from Texas Instruments Code Composer Studio (C/C++) output files")
public class TiCcsConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new TiCcsConsoleExtractor(getFiles()));
	}

}
