package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.unittest.xunit.FPCUnitExtractor;

@Command(name = "fpcunit", description = "Add information from a FPCUnit XML file")
public class FPCUnitExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "xml", description = "XML file or folder to parse", required = true)
	public File xml;
	
	@Override
	public void execute() {
		buildHealth.extract(new FPCUnitExtractor(new PseudoFiles(xml)));
	}
	
}