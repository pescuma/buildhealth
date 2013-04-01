package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.CPDExtractor;

@Command(name = "cpd", description = "Add code duplication information from a CPD TXT file")
public class CPDExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with CPD TXT output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new CPDExtractor(new PseudoFiles(file)));
	}
	
}
