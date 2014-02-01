package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.JavaDocConsoleExtractor;

@Command(name = "javadoc-console", description = "Add warnings from JavaDoc Tool output files")
public class JavaDocConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with JavaDoc Tool output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new JavaDocConsoleExtractor(new PseudoFiles(file)));
	}

}
