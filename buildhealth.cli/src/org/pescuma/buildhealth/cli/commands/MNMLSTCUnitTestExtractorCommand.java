package org.pescuma.buildhealth.cli.commands;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.xunit.MNMLSTCUnitTestExtractor;

@Command(name = "mnmlstcUnittest", description = "Add information from a MNMLSTC unittest XML file")
public class MNMLSTCUnitTestExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "xml", description = "XML file or folder to parse", required = true)
	public File xml;
	
	@Override
	public void run() {
		getBuildHealth().extract(new MNMLSTCUnitTestExtractor(new PseudoFiles(xml)));
	}
	
}
