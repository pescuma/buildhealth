package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.YuiCompressorConsoleExtractor;

@Command(name = "yui-compressor-console", description = "Add warnings from YUI Compressor output files")
public class YuiCompressorConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with YUI Compressor output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new YuiCompressorConsoleExtractor(new PseudoFiles(file)));
	}

}
