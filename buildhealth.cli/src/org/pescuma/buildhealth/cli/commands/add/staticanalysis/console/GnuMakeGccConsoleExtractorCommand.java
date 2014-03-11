package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.GnuMakeGccConsoleExtractor;

@Command(name = "gnu-make-gcc-console", description = "Add warnings from GNU Make + GNU C Compiler (gcc) output files")
public class GnuMakeGccConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new GnuMakeGccConsoleExtractor(getFiles()));
	}

}
