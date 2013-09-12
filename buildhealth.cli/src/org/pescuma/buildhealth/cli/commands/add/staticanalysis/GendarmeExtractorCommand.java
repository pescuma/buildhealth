package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.GendarmeExtractor;

@Command(name = "gendarme", description = "Add static code analysis from a Gendarme XML file")
public class GendarmeExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with Gendarme XML output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new GendarmeExtractor(new PseudoFiles(file)));
	}
	
}
