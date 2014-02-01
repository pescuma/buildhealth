package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.IntelCConsoleExtractor;

@Command(name = "intel-c", description = "Add warnings from Intel C Compiler output files")
public class IntelCConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Intel C Compiler output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new IntelCConsoleExtractor(new PseudoFiles(file)));
	}

}
