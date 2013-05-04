package org.pescuma.buildhealth.cli.commands.add.japex;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.performance.JapexExtractor;

@Command(name = "japex", description = "Add performance information from an Japex report XML file")
public class JapexExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with japex XML report(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new JapexExtractor(new PseudoFiles(file)));
	}
	
}
