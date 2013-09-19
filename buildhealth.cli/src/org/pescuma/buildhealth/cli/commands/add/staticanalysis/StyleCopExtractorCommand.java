package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.StyleCopExtractor;

@Command(name = "stylecop", description = "Add static code analysis from a StyleCop XML file")
public class StyleCopExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with StyleCop XML output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new StyleCopExtractor(new PseudoFiles(file)));
	}
	
}
