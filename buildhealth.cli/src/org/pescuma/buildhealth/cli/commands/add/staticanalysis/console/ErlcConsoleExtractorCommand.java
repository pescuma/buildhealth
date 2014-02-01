package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.ErlcConsoleExtractor;

@Command(name = "erlc-console", description = "Add warnings from Erlang Compiler (erlc) output files")
public class ErlcConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Erlang Compiler (erlc) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new ErlcConsoleExtractor(new PseudoFiles(file)));
	}

}
