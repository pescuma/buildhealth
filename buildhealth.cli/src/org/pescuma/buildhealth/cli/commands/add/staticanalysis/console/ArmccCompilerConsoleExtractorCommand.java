package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.ArmccCompilerConsoleExtractor;

@Command(name = "armcc", description = "Add warnings from Armcc Compiler output files")
public class ArmccCompilerConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Armcc Compiler output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new ArmccCompilerConsoleExtractor(new PseudoFiles(file)));
	}

}
