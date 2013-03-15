package org.pescuma.buildhealth.cli.commands.unittest;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.xunit.CppTestExtractor;

@Command(name = "cpptest", description = "Add information from a CppTest XML file")
public class CppTestExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "xml", description = "XML file or folder to parse", required = true)
	public File xml;
	
	@Override
	public void run() {
		getBuildHealth().extract(new CppTestExtractor(new PseudoFiles(xml)));
	}
	
}
