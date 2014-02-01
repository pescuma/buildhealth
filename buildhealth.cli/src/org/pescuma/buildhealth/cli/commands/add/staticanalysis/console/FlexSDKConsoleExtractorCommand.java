package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.FlexSDKConsoleExtractor;

@Command(name = "flex-console", description = "Add warnings from Flex SDK Compilers (compc & mxmlc) output files")
public class FlexSDKConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Flex SDK Compilers (compc & mxmlc) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new FlexSDKConsoleExtractor(new PseudoFiles(file)));
	}

}
