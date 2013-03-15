package org.pescuma.buildhealth.cli.commands.unittest;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.xunit.TusarExtractor;

@Command(name = "tusar", description = "Add information from a Tusar XML file")
public class TusarExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "xml", description = "XML file or folder to parse", required = true)
	public File xml;
	
	@Override
	public void run() {
		getBuildHealth().extract(new TusarExtractor(new PseudoFiles(xml)));
	}
	
}
