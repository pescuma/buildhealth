package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.SunCConsoleExtractor;

@Command(name = "sun-c-console", description = "Add warnings from SUN C++ Compiler output files")
public class SunCConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with SUN C++ Compiler output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new SunCConsoleExtractor(new PseudoFiles(file)));
	}

}
