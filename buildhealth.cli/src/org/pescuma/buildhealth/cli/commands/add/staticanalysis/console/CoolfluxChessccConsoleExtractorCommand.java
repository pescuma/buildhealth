package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.CoolfluxChessccConsoleExtractor;

@Command(name = "chesscc-console", description = "Add warnings from Coolflux DSP Compiler (chesscc) output files")
public class CoolfluxChessccConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Coolflux DSP Compiler (chesscc) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new CoolfluxChessccConsoleExtractor(new PseudoFiles(file)));
	}

}
