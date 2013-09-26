package org.pescuma.buildhealth.cli.commands.add.tasks;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.tasks.BugsEverywhereExtractor;

@Command(name = "be", description = "Add tasks from BugsEverywhere (the output of be list)")
public class BugsEverywhereExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with 'be list' output(s)", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new BugsEverywhereExtractor(new PseudoFiles(file)));
	}
	
}
