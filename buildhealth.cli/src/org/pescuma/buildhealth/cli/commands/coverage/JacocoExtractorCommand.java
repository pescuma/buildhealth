package org.pescuma.buildhealth.cli.commands.coverage;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.jacoco.JacocoExtractor;

@Command(name = "jacoco", description = "Add coverage information from an JaCoCo XML file")
public class JacocoExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with JaCoCo xml output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new JacocoExtractor(new PseudoFiles(file)));
	}
	
}
