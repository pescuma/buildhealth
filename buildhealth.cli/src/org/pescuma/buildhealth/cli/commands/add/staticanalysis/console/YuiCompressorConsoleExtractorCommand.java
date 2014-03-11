package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.staticanalysis.console.YuiCompressorConsoleExtractor;

@Command(name = "yui-compressor-console", description = "Add warnings from YUI Compressor output files")
public class YuiCompressorConsoleExtractorCommand extends BaseBuildHealthFilesCliCommand {

	@Override
	public void execute() {
		buildHealth.extract(new YuiCompressorConsoleExtractor(getFiles()));
	}

}
