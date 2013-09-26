package org.pescuma.buildhealth.cli.commands.add.tasks;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.tasks.TFSWorkItemsExtractor;

@Command(name = "tfs-wi", description = "Add TFS work items (generated from 'tfpt query /collection:uri /format:xml')")
public class TFSWorkItemsExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file or folder", description = "File or folder with work item(s) in XML", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new TFSWorkItemsExtractor(new PseudoFiles(file)));
	}
	
}
