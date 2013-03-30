package org.pescuma.buildhealth.cli.commands.staticanalysis;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.extractor.PseudoFiles;
import org.pescuma.buildhealth.extractor.staticanalysis.TasksExtractor;

@Command(name = "tasks", description = "Extract tasks from source files")
public class TasksExtractorCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file", description = "Source file or folder to parse", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.extract(new TasksExtractor(new PseudoFiles(file)));
	}
	
}
