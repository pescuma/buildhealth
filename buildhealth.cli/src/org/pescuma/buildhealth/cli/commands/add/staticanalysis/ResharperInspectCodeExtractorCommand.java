package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.ResharperInspectCodeExtractor;

@Command(name = "resharper-inspectcode", description = "Add code duplication information from ReSharper InspectCode output file")
public class ResharperInspectCodeExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with InspectCode XML output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new ResharperInspectCodeExtractor(new PseudoFiles(file)));
	}
	
}
