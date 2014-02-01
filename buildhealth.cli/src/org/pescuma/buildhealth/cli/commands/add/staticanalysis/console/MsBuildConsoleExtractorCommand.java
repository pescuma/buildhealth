package org.pescuma.buildhealth.cli.commands.add.staticanalysis.console;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.console.MsBuildConsoleExtractor;

@Command(name = "msbuild", description = "Add warnings from MSBuild output files")
public class MsBuildConsoleExtractorCommand extends BuildHealthCliCommand {

	@Arguments(title = "file or folder", description = "File or folder with MSBuild output(s)", required = true)
	public File file;

	@Override
	public void execute() {
		buildHealth.extract(new MsBuildConsoleExtractor(new PseudoFiles(file)));
	}

}
