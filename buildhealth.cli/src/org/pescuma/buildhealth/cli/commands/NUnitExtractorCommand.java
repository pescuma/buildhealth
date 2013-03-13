package org.pescuma.buildhealth.cli.commands;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.xunit.NUnitExtractor;

@Command(name = "nunit", description = "Add information from a NUnit XML file")
public class NUnitExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "xml", description = "XML file or folder to parse", required = true)
	public File xml;
	
	@Override
	public void run() {
		getBuildHealth().extract(new NUnitExtractor(xml));
	}
	
}
