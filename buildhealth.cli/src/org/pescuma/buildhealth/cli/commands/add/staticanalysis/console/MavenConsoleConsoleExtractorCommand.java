package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.MavenConsoleConsoleExtractor;

@Command(name = "maven-console", description = "Add warnings from Maven output files")
public class MavenConsoleConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with Maven output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new MavenConsoleConsoleExtractor(new PseudoFiles(file)));
	}

}
