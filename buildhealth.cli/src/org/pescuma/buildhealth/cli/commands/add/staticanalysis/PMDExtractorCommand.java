package org.pescuma.buildhealth.cli.commands.add.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.PMDExtractor;

@Command(name = "pmd", description = "Add static code analysis from a PMD XML file")
public class PMDExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with PMD xml output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new PMDExtractor(new PseudoFiles(file)));
	}
	
}
