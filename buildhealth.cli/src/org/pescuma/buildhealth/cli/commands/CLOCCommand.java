package org.pescuma.buildhealth.cli.commands;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.loc.CLOCExtractor;

@Command(name = "cloc", description = "Add lines of code information from cloc csv output")
public class CLOCCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with cloc csv output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new CLOCExtractor(new PseudoFiles(file)));
	}
	
}
