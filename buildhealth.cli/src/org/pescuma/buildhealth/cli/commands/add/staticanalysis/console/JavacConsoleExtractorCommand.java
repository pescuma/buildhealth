package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.JavacConsoleExtractor;

@Command(name = "javac", description = "Add warnings from Java Compiler (javac) output files")
public class JavacConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Java Compiler (javac) output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new JavacConsoleExtractor(new PseudoFiles(file)));
	}

}
