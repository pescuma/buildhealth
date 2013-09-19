package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.FxCopExtractor;

@Command(name = "fxcop", description = "Add static code analysis from a FxCop XML report file")
public class FxCopExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with FxCop XML report(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new FxCopExtractor(new PseudoFiles(file)));
	}
	
}
