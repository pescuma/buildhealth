package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.FindBugsExtractor;

@Command(name = "findbugs", description = "Add code duplication information from a FindBugs XML file")
public class FindBugsExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with FindBugs XML output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new FindBugsExtractor(new PseudoFiles(file)));
	}
	
}
