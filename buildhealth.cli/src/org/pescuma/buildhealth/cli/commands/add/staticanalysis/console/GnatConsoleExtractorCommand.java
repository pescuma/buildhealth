package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.GnatConsoleExtractor;

@Command(name = "gnat-console", description = "Add warnings from Ada Compiler (gnat) output files")
public class GnatConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new GnatConsoleExtractor(getFiles()));
	}

}
