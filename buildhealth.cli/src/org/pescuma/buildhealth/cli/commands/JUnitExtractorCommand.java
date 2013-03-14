package org.pescuma.buildhealth.cli.commands;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.junit.JUnitExtractor;

@Command(name = "junit", description = "Add information from a JUnit XML file")
public class JUnitExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "xml", description = "XML file or folder to parse", required = true)
	public File xml;
	
	@Override
	public void run() {
		getBuildHealth().extract(new JUnitExtractor(new PseudoFiles(xml)));
	}
	
}
