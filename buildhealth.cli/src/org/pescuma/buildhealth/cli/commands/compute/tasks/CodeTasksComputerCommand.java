package org.pescuma.buildhealth.cli.commands.compute.tasks;

import io.airlift.airline.Command;

import org.pescuma.buildhealth.cli.BaseBuildHealthFilesCliCommand;
import org.pescuma.buildhealth.computer.tasks.CodeTasksComputer;

@Command(name = "tasks", description = "Extract tasks from source files")
public class CodeTasksComputerCommand extends BaseBuildHealthFilesCliCommand {
	
	@Override
	public void execute() {
		buildHealth.compute(new CodeTasksComputer(getFiles()));
	}
	
}
