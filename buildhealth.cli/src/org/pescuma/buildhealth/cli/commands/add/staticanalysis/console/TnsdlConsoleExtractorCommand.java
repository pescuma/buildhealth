package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.TnsdlConsoleExtractor;

@Command(name = "tnsdl", description = "Add warnings from TNSDL Translator output files")
public class TnsdlConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with TNSDL Translator output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new TnsdlConsoleExtractor(new PseudoFiles(file)));
	}

}
