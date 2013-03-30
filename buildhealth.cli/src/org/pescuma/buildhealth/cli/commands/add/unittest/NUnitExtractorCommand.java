package org.pescuma.buildhealth.cli.commands.add.unittest;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.unittest.xunit.NUnitExtractor;

@Command(name = "nunit", description = "Add information from a NUnit XML file")
public class NUnitExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "xml", description = "XML file or folder to parse", required = true)
	public File xml;
	
	@Override
	public void execute() {
		buildHealth.extract(new NUnitExtractor(new PseudoFiles(xml)));
	}
	
}
