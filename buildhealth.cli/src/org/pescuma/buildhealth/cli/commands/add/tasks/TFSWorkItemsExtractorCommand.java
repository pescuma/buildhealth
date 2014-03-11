package org.pescuma.buildhealth.cli.commands.add.tasks;

import io.airlift.command.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.extractor.tasks.TFSWorkItemsExtractor;

@Command(name = "tfs-wi", description = "Add TFS work items from a XML file (generated from 'tfpt query /collection:uri /format:xml')")
public class TFSWorkItemsExtractorCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.extract(new TFSWorkItemsExtractor(getFiles()));
	}
	
}
