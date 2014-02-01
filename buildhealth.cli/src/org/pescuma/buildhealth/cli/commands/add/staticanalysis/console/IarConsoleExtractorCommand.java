package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.IarConsoleExtractor;

@Command(name = "iar-console", description = "Add warnings from IAR Compiler (C/C++) output files")
public class IarConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with IAR Compiler (C/C++) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new IarConsoleExtractor(new PseudoFiles(file)));
	}

}
