package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.ResharperDupFinderExtractor;

@Command(name = "resharper-dupfinder", description = "Add code duplication information from ReSharper DupFinder output file")
public class ResharperDupFinderExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with DupFinder XML output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new ResharperDupFinderExtractor(new PseudoFiles(file)));
	}
	
}
