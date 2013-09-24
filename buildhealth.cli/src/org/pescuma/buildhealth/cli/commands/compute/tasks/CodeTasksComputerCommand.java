package org.pescuma.buildhealth.cli.commands.compute.tasks;

import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.io.File;

import org.pescuma.buildhealth.cli.BuildHealthCliCommand;
import org.pescuma.buildhealth.computer.tasks.CodeTasksComputer;
import org.pescuma.buildhealth.extractor.PseudoFiles;

@Command(name = "tasks", description = "Extract tasks from source files")
public class CodeTasksComputerCommand extends BuildHealthCliCommand {
	
	@Arguments(title = "file", description = "Source file or folder to parse", required = true)
	public File file;
	
	@Override
	public void execute() {
		buildHealth.compute(new CodeTasksComputer(new PseudoFiles(file)));
	}
	
}
