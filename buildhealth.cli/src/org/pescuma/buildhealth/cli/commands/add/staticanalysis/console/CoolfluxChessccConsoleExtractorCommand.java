package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.CoolfluxChessccConsoleExtractor;

@Command(name = "chesscc-console", description = "Add warnings from Coolflux DSP Compiler (chesscc) output files")
public class CoolfluxChessccConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new CoolfluxChessccConsoleExtractor(getFiles()));
	}
	
}
