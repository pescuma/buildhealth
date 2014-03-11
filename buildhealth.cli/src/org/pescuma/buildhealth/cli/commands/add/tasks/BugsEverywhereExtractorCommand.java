package org.pescuma.buildhealth.cli.commands.add.tasks;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.tasks.BugsEverywhereExtractor;

@Command(name = "be", description = "Add tasks from BugsEverywhere (the output of 'be show -xml')")
public class BugsEverywhereExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new BugsEverywhereExtractor(getFiles()));
	}
	
}
