package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.GnatConsoleExtractor;

@Command(name = "gnat", description = "Add warnings from Ada Compiler (gnat) output files")
public class GnatConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Ada Compiler (gnat) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new GnatConsoleExtractor(new PseudoFiles(file)));
	}

}
